package org.playuniverse.minecraft.wildcard.bungee;

import java.util.concurrent.Executor;

import org.playuniverse.minecraft.wildcard.bungee.command.BungeeCommand;
import org.playuniverse.minecraft.wildcard.bungee.listener.PlayerListener;
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

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public final class WildcardBungee extends Plugin implements IWildcardPlugin {

    private final BungeeAdapter adapter = new BungeeAdapter();
    private final WildcardCore core;

    private final BungeeExecutor executor = new BungeeExecutor(this);
    private final Container<BungeeService> service = Container.of();

    private boolean setup = false;

    public WildcardBungee() {
        this.core = new WildcardCore(this);
        Singleton.get(BungeeVersionProvider.class);
    }

    /*
     * 
     */

    @Override
    public void onEnable() {
        if (!core.enable()) {
            onDisable();
            return;
        }
        onSetup();
    }

    public void onSetup() {
        if (setup) {
            return;
        }
        setup = true;
        core.preSetup();
        service.replace(new BungeeService(core.getComponentParser(), Singleton.get(PluginSettings.class)));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener(core, core.getDatabase()));
        core.postSetup();
    }

    @Override
    public void onDisable() {
        core.disable();
    }

    @Override
    public BaseCommand<?> build(final RootNode<BaseInfo> node, final String[] aliases) {
        return new BungeeCommand(core, new NodeRedirect<>(new MapNode<>(i -> i, node)), node.getName(), aliases);
    }

    /*
     * Getter
     */

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public String getName() {
        return getDescription().getName();
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
