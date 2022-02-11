package me.lauriichan.minecraft.wildcard.bungee;

import java.io.File;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;

import org.sqlite.JDBC;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.bungee.command.BungeeCommand;
import me.lauriichan.minecraft.wildcard.bungee.inject.BungeeCommands;
import me.lauriichan.minecraft.wildcard.bungee.listener.PlayerListener;
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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public final class WildcardBungee extends Plugin implements IWildcardPlugin {

    private final BungeeAdapter adapter = new BungeeAdapter();
    private final WildcardCore core;

    private final BungeeExecutor executor = new BungeeExecutor(this);
    private final Container<BungeeService> service = Container.of();

    private boolean setup = false;

    public WildcardBungee() {
        registerSQLite();
        this.core = new WildcardCore(this);
        Singleton.get(BungeeVersionProvider.class);
    }

    private void registerSQLite() {
        try {
            DriverManager.registerDriver(new JDBC());
        } catch (Exception exp) {
            // Ignore
        }
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
        core.getInjections().register(new BungeeCommands());
        core.registerCommands();
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
    public ExecutorService getExecutor() {
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
    
    @Override
    public File getJarFile() {
        return getFile();
    }

}
