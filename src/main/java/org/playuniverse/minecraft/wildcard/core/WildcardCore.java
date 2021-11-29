package org.playuniverse.minecraft.wildcard.core;

import java.util.Optional;
import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.command.Command;
import org.playuniverse.minecraft.wildcard.core.command.IBasicCommand;
import org.playuniverse.minecraft.wildcard.core.command.WildcardCommand;
import org.playuniverse.minecraft.wildcard.core.data.setting.Settings;
import org.playuniverse.minecraft.wildcard.core.data.setting.converter.FileConverter;
import org.playuniverse.minecraft.wildcard.core.data.setting.converter.ListConverter;
import org.playuniverse.minecraft.wildcard.core.data.setting.converter.MapConverter;
import org.playuniverse.minecraft.wildcard.core.data.setting.converter.TranslationConverter;
import org.playuniverse.minecraft.wildcard.core.data.setting.json.JsonIO;
import org.playuniverse.minecraft.wildcard.core.data.storage.Database;
import org.playuniverse.minecraft.wildcard.core.data.storage.DatabaseInitializationException;
import org.playuniverse.minecraft.wildcard.core.data.storage.mysql.MySQLDatabase;
import org.playuniverse.minecraft.wildcard.core.data.storage.sqlite.SQLiteDatabase;
import org.playuniverse.minecraft.wildcard.core.settings.DatabaseSettings;
import org.playuniverse.minecraft.wildcard.core.settings.PluginSettings;
import org.playuniverse.minecraft.wildcard.core.settings.Translation;
import org.playuniverse.minecraft.wildcard.core.settings.WebSettings;
import org.playuniverse.minecraft.wildcard.core.util.JavaLogger;
import org.playuniverse.minecraft.wildcard.core.util.ReflectHelper;
import org.playuniverse.minecraft.wildcard.core.util.Singleton;
import org.playuniverse.minecraft.wildcard.core.util.inject.Injections;
import org.playuniverse.minecraft.wildcard.core.util.reflection.ClassLookupProvider;
import org.playuniverse.minecraft.wildcard.core.util.tick.TickTimer;
import org.playuniverse.minecraft.wildcard.core.web.WebControl;
import org.playuniverse.minecraft.wildcard.core.web.listener.CommandListener;
import org.playuniverse.minecraft.wildcard.core.web.listener.PathListener;
import org.playuniverse.minecraft.wildcard.core.web.listener.PlaceholderListener;

import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public final class WildcardCore {

    public static final UUID SERVER_UID = new UUID(0, 0);

    private final EventManager eventManager;

    private final IWildcardPlugin plugin;
    private final ILogger logger;

    private final Container<WebControl> control = Container.of();
    private final Container<Database> database = Container.of();

    private final TickTimer cacheTimer = new TickTimer(1000, 0);

    private final Container<ClassLookupProvider> classProvider = Container.of();
    private final Container<Injections> injections = Container.of();

    public WildcardCore(final IWildcardPlugin plugin) {
        this.plugin = plugin;
        this.logger = new JavaLogger(plugin.getAdapter());
        Singleton.inject(this);
        Singleton.inject(logger);
        this.eventManager = new EventManager(logger);
        eventManager.registerEvents(new PathListener());
        eventManager.registerEvents(new CommandListener());
        eventManager.registerEvents(new PlaceholderListener());
    }

    /*
     * 
     */

    public boolean enable() {
        load();
        if (!loadDatabase()) {
            return false;
        }
        control.get().load();
        cacheTimer.start();
        return true;
    }

    private void load() {
        JsonIO.register(new TranslationConverter());
        JsonIO.register(new FileConverter());
        JsonIO.register(new ListConverter());
        JsonIO.register(new MapConverter());
        JsonIO.PARSER.getClass();
        JsonIO.WRITER.getClass();
        Singleton.get(Settings.class).load();
        Singleton.get(PluginSettings.class).load();
        Singleton.get(WebSettings.class).load();
        Translation.load();
        if (control.isEmpty()) {
            control.replace(new WebControl(this));
        }
    }

    public boolean reload() {
        cacheTimer.pause();
        Singleton.get(Settings.class).clear();
        database.ifPresent(Database::close);
        return enable();
    }

    public void preSetup() {
        classProvider.replace(new ClassLookupProvider());
        injections.replace(new Injections(classProvider.get())).get().setup();
    }

    public void postSetup() {
        register(new WildcardCommand());
        cacheTimer.add(plugin.getService());
    }

    public void disable() {
        cacheTimer.stop();
        control.ifPresent(WebControl::exit);
        database.ifPresent(Database::close);
        Singleton.get(Settings.class).save();
    }

    /*
     * Helper
     */

    private boolean loadDatabase() {
        final DatabaseSettings settings = Singleton.get(DatabaseSettings.class);
        final PluginSettings pluginSettings = Singleton.get(PluginSettings.class);
        settings.load();
        final String type = settings.getString("type", "sqlite").toLowerCase();
        Database database = null;
        try {
            switch (type) {
            case "mysql":
                database = new MySQLDatabase(logger, plugin.getExecutor(), cacheTimer, pluginSettings, settings);
                break;
            default:
                logger.log(LogTypeId.WARNING, "Unknown database type '" + type + "' falling back to 'sqlite'!");
            case "sqlite":
                break;
            }
        } catch (final DatabaseInitializationException exp) {
            database = null;
            logger.log(LogTypeId.WARNING, "Failed to initialize database of type '" + type + "' falling back to 'sqlite'!");
            logger.log(LogTypeId.WARNING, exp);
        }
        if (database == null) {
            try {
                database = new SQLiteDatabase(logger, plugin.getExecutor(), cacheTimer, pluginSettings, settings, plugin.getDataFolder());
            } catch (final DatabaseInitializationException exp) {
                logger.log(LogTypeId.WARNING, "Failed to initialize database of type 'sqlite'!");
                logger.log(LogTypeId.WARNING, exp);
                logger.log(LogTypeId.ERROR, "No database was able to initialize!");
                logger.log(LogTypeId.ERROR, "Disabling Wildcard...");
                return false;
            }
        }
        this.database.replace(database);
        return true;
    }

    public void register(final IBasicCommand command) {
        if (command == null) {
            return;
        }
        final Optional<Command> option = ReflectHelper.getAnnotationOfMethod(Command.class, command.getClass(), "build", String.class);
        if (option.isEmpty()) {
            return;
        }
        final Command info = option.get();
        if (info.name() == null || info.name().isBlank()) {
            return;
        }
        inject(plugin.build(command.build(info.name()), info.aliases()));
    }

    /*
     * Getter
     */

    public ILogger getLogger() {
        return logger;
    }

    public WebControl getWebControl() {
        return control.get();
    }

    public Injections getInjections() {
        return injections.get();
    }

    public IWildcardPlugin getPlugin() {
        return plugin;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public ClassLookupProvider getClassProvider() {
        return classProvider.get();
    }

    public Container<Database> getDatabase() {
        return database;
    }

    /*
     * Helper
     */

    public boolean inject(final Object object) {
        return injections.get().inject(object);
    }

}