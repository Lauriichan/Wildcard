package me.lauriichan.minecraft.wildcard.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.lauriichan.minecraft.wildcard.core.util.Singleton;

@Mod("wildcard")
public class WildcardForge {

    private static final Logger LOGGER = LogManager.getLogger();

    public WildcardForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        Singleton.get(ForgeVersionProvider.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        
    }
}
