package me.lauriichan.minecraft.wildcard.sponge;

import java.io.File;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.sqlite.JDBC;

import com.google.inject.Inject;
import com.syntaxphoenix.syntaxapi.json.io.JsonWriter;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.IWildcardPlugin;
import me.lauriichan.minecraft.wildcard.core.ServiceAdapter;
import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseCommand;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseInfo;
import me.lauriichan.minecraft.wildcard.core.command.api.nodes.MapNode;
import me.lauriichan.minecraft.wildcard.core.command.api.nodes.RootNode;
import me.lauriichan.minecraft.wildcard.core.command.api.redirect.NodeRedirect;
import me.lauriichan.minecraft.wildcard.core.settings.PluginSettings;
import me.lauriichan.minecraft.wildcard.core.util.Singleton;
import me.lauriichan.minecraft.wildcard.sponge.command.SpongeCommand;
import me.lauriichan.minecraft.wildcard.sponge.inject.SpongeCommands;
import me.lauriichan.minecraft.wildcard.sponge.listener.PlayerListener;

@Plugin(id = "wildcard", name = "Wildcard")
public final class WildcardSponge implements IWildcardPlugin {

    private final Logger logger;
    private final PluginContainer container;

    private final SpongeAdapter adapter;
    private final WildcardCore core;

    private final SpongeExecutor executor;
    private final Container<SpongeService> service = Container.of();
    private final Container<File> jarFile = Container.of();

    private final File dataFolder;

    private boolean setup = false;

    @Inject
    WildcardSponge(final Logger logger, final PluginContainer container) {
        registerSQLite();
        this.logger = logger;
        this.container = container;
        this.dataFolder = Sponge.getConfigManager().getPluginConfig(container).getDirectory().toFile();
        this.adapter = new SpongeAdapter(this);
        this.executor = new SpongeExecutor(container);
        this.core = new WildcardCore(this);
    }

    private void registerSQLite() {
        try {
            DriverManager.registerDriver(new JDBC());
        } catch (Exception exp) {
            // Ignore
        }
    }

    @Listener
    public void onStart(final GameStartedServerEvent event) {
        if(Sponge.getPlatform().getExecutionType() != Platform.Type.SERVER) {
            return;
        }
        if (!core.enable()) {
            onStop(null);
            return;
        }
        JsonWriter.class.getClass();
        onSetup();
    }

    public void onSetup() {
        if (setup) {
            return;
        }
        setup = true;
        core.preSetup();
        core.getInjections().register(new SpongeCommands());
        core.registerCommands();
        service.replace(new SpongeService(core.getComponentParser(), Singleton.get(PluginSettings.class)));
        Sponge.getEventManager().registerListeners(container, new PlayerListener(core, core.getDatabase()));
        core.postSetup();
    }

    @Listener
    public void onStop(final GameStoppingServerEvent event) {
        if(Sponge.getPlatform().getExecutionType() != Platform.Type.SERVER) {
            return;
        }
        core.disable();
    }

    @Override
    public BaseCommand<?> build(RootNode<BaseInfo> node, String[] aliases) {
        return new SpongeCommand(core, new NodeRedirect<>(new MapNode<>(i -> i, node)), node.getName(), aliases);
    }

    @Override
    public String getName() {
        return container.getName();
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public WildcardCore getCore() {
        return core;
    }

    @Override
    public ServiceAdapter getService() {
        return service.get();
    }

    @Override
    public IWildcardAdapter getAdapter() {
        return adapter;
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginContainer getContainer() {
        return container;
    }

    @Override
    public File getJarFile() {
        if (jarFile.isPresent()) {
            return jarFile.get();
        }
        File file = jarFileHack();
        if (file != null) {
            return jarFile.replace(file).lock().get();
        }
        return jarFile.replace(new File(WildcardSponge.class.getProtectionDomain().getCodeSource().getLocation().getPath())).lock().get();
    }

    /*
     * Why do we even have to do this..
     * Why can't they just provide the jar..
     * I see no reason in doing it like this but there is no other way...
     */
    private File jarFileHack() {
        return container.getSource().map(path -> path.toFile()).orElse(null);
    }

}
