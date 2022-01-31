package me.lauriichan.minecraft.wildcard.sponge;

import java.io.File;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

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

@Plugin("wildcard")
public final class WildcardSponge implements IWildcardPlugin {

    private final Logger logger;
    private final PluginContainer container;

    private final SpongeAdapter adapter;
    private final WildcardCore core;

    private final SpongeExecutor executor;
    private final Container<SpongeService> service = Container.of();

    private final File dataFolder;
    private final SpongeConfiguration config;

    private boolean setup = false;

    @Inject
    WildcardSponge(final Logger logger, final PluginContainer container) {
        this.logger = logger;
        this.container = container;
        this.config = new SpongeConfiguration(Sponge.configManager().sharedConfig(container).configPath(), "config.conf");
        this.dataFolder = Sponge.configManager().pluginConfig(container).directory().toFile();
        this.adapter = new SpongeAdapter(this);
        this.executor = new SpongeExecutor(container);
        this.core = new WildcardCore(this);
        Singleton.get(SpongeVersionProvider.class);
    }

    @Listener
    public void onStart(final StartingEngineEvent<Server> event) {
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
        service.replace(new SpongeService(core.getComponentParser(), Singleton.get(PluginSettings.class)));
        Sponge.eventManager().registerListeners(container, new PlayerListener(core, core.getDatabase()));
        core.getInjections().register(new SpongeCommands());
        core.registerCommands();
        core.postSetup();
    }

    @Listener
    public void onStop(final StoppingEngineEvent<Server> event) {
        core.disable();
    }

    @Override
    public BaseCommand<?> build(RootNode<BaseInfo> node, String[] aliases) {
        return new SpongeCommand(core, new NodeRedirect<>(new MapNode<>(i -> i, node)), node.getName(), aliases);
    }

    @Override
    public String getName() {
        return container.metadata().name().orElse(container.metadata().id());
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public Executor getExecutor() {
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

    public SpongeConfiguration getConfig() {
        return config;
    }

    public PluginContainer getContainer() {
        return container;
    }

}