package org.playuniverse.minecraft.wildcard.spigot;

import java.util.concurrent.Executor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.playuniverse.minecraft.wildcard.core.IWildcardAdapter;
import org.playuniverse.minecraft.wildcard.core.IWildcardPlugin;
import org.playuniverse.minecraft.wildcard.core.ServiceAdapter;
import org.playuniverse.minecraft.wildcard.core.WildcardCore;
import org.playuniverse.minecraft.wildcard.core.command.api.base.BaseCommand;
import org.playuniverse.minecraft.wildcard.core.command.api.base.BaseInfo;
import org.playuniverse.minecraft.wildcard.core.command.api.nodes.MapNode;
import org.playuniverse.minecraft.wildcard.core.command.api.nodes.RootNode;
import org.playuniverse.minecraft.wildcard.core.command.api.redirect.NodeRedirect;
import org.playuniverse.minecraft.wildcard.core.settings.PluginSettings;
import org.playuniverse.minecraft.wildcard.core.util.Singleton;
import org.playuniverse.minecraft.wildcard.spigot.command.SpigotCommand;
import org.playuniverse.minecraft.wildcard.spigot.inject.SpigotCommands;
import org.playuniverse.minecraft.wildcard.spigot.listener.PlayerListener;

import com.syntaxphoenix.syntaxapi.json.io.JsonWriter;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public final class WildcardSpigot extends JavaPlugin implements IWildcardPlugin {

    private final SpigotAdapter adapter = SpigotAdapter.build();
    private final WildcardCore core;

    private final SpigotExecutor executor = new SpigotExecutor(this);
    private final Container<SpigotService> service = Container.of();

    private boolean setup = false;

    public WildcardSpigot() {
        this.core = new WildcardCore(this);
        Singleton.get(SpigotVersionProvider.class);
    }

    /*
     * 
     */

    @Override
    public void onEnable() {
        if (!core.enable()) {
            Bukkit.getPluginManager().disablePlugin(this);
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
        service.replace(new SpigotService(core.getComponentParser(), Singleton.get(PluginSettings.class)));
        Bukkit.getPluginManager().registerEvents(new PlayerListener(core, core.getDatabase()), this);
        core.getInjections().register(new SpigotCommands());
        core.postSetup();
    }

    @Override
    public void onDisable() {
        core.disable();
    }

    @Override
    public BaseCommand<?> build(final RootNode<BaseInfo> node, final String[] aliases) {
        return new SpigotCommand(core, new NodeRedirect<>(new MapNode<>(i -> i, node)), node.getName(), aliases);
    }

    /*
     * Getter
     */

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

}
