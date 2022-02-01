package me.lauriichan.minecraft.wildcard.fabric;

import java.io.File;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sqlite.JDBC;

import com.syntaxphoenix.syntaxapi.json.io.JsonWriter;

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
import me.lauriichan.minecraft.wildcard.core.util.NamedThreadFactory;
import me.lauriichan.minecraft.wildcard.core.util.Singleton;
import me.lauriichan.minecraft.wildcard.fabric.command.FabricCommand;
import me.lauriichan.minecraft.wildcard.fabric.inject.FabricCommands;
import me.lauriichan.minecraft.wildcard.fabric.listener.PlayerListener;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class WildcardFabric implements DedicatedServerModInitializer, IWildcardPlugin {

    public static final String MODID = "wildcard";

    private final WildcardCore core;

    private final File dataFolder;
    private final File jarFile;

    private final NamedThreadFactory threadFactory = new NamedThreadFactory("Wildcard");
    private final ExecutorService threadService = Executors.newCachedThreadPool(threadFactory);

    private boolean setup = false;

    public WildcardFabric() {
        jarFile = FabricLoader.getInstance().getModContainer(MODID).get().getRootPath().toFile();
        registerSQLite();
        this.dataFolder = new File(MODID);
        this.core = new WildcardCore(this);
    }

    private void registerSQLite() {
        try {
            DriverManager.registerDriver(new JDBC());
        } catch (Exception exp) {
            // Ignore
        }
    }

    @Override
    public void onInitializeServer() {
        if (!core.enable()) {
            onStop();
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
        PluginSettings settings = Singleton.get(PluginSettings.class);
        settings.getString("server.name", "Minecraft Server");
        new PlayerListener(core, core.getDatabase());
        core.getInjections().register(new FabricCommands());
        core.registerCommands();
        core.postSetup();
    }

    public void onStop() {
        core.disable();
    }

    @Override
    public BaseCommand<?> build(RootNode<BaseInfo> node, String[] alias) {
        return new FabricCommand(core, new NodeRedirect<>(new MapNode<>(i -> i, node)), node.getName(), alias);
    }

    @Override
    public IWildcardAdapter getAdapter() {
        return null;
    }

    @Override
    public WildcardCore getCore() {
        return core;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public ExecutorService getExecutor() {
        return threadService;
    }

    @Override
    public File getJarFile() {
        return jarFile;
    }

    @Override
    public String getName() {
        return "Wildcard";
    }

    @Override
    public ServiceAdapter getService() {
        return null;
    }

}
