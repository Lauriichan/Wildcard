package me.lauriichan.minecraft.wildcard.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import java.io.File;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.tuple.Pair;
import org.sqlite.JDBC;

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
import me.lauriichan.minecraft.wildcard.core.util.NamedThreadFactory;
import me.lauriichan.minecraft.wildcard.core.util.Singleton;
import me.lauriichan.minecraft.wildcard.forge.command.ForgeCommand;
import me.lauriichan.minecraft.wildcard.forge.inject.ForgeCommands;
import me.lauriichan.minecraft.wildcard.forge.listener.PlayerListener;

@Mod("wildcard")
public class WildcardForge implements IWildcardPlugin {

    public static final String MODID = "wildcard";

    private final ForgeAdapter adapter;
    private final WildcardCore core;

    private final Container<ForgeService> service = Container.of();

    private final NamedThreadFactory threadFactory = new NamedThreadFactory("Wildcard");
    private final ExecutorService threadService = Executors.newCachedThreadPool(threadFactory);

    private final File dataFolder;
    private final File jarFile;

    private boolean setup = false;

    public WildcardForge() {
        jarFile = ((ModFileInfo) ModLoadingContext.get().getActiveContainer().getModInfo().getOwningFile()).getFile().getFilePath().toFile();
        registerSQLite();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
            () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        ForgeConfiguration.register(ModLoadingContext.get());
        this.dataFolder = new File(ModLoadingContext.get().getActiveNamespace());
        this.adapter = new ForgeAdapter();
        this.core = new WildcardCore(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerSQLite() {
        try {
            DriverManager.registerDriver(new JDBC());
        } catch (Exception exp) {
            // Ignore
        }
    }

    @SubscribeEvent
    public void onStart(final FMLServerStartingEvent event) {
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
        service.replace(new ForgeService(core.getComponentParser(), Singleton.get(PluginSettings.class)));
        new PlayerListener(core, core.getDatabase());
        core.getInjections().register(new ForgeCommands());
        core.registerCommands();
        core.postSetup();
    }

    @SubscribeEvent
    public void onStop(final FMLServerStoppingEvent event) {
        core.disable();
    }

    @Override
    public BaseCommand<?> build(RootNode<BaseInfo> node, String[] alias) {
        return new ForgeCommand(core, new NodeRedirect<>(new MapNode<>(i -> i, node)), node.getName(), alias);
    }

    @Override
    public String getName() {
        return "Wildcard"; // Hardcore it this time
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
        return jarFile;
    }
}
