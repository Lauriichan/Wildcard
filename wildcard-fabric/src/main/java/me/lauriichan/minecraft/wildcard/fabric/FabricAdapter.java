package me.lauriichan.minecraft.wildcard.fabric;

import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.settings.PluginSettings;
import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;
import me.lauriichan.minecraft.wildcard.core.util.Singleton;
import me.lauriichan.minecraft.wildcard.fabric.component.FabricComponentAdapter;

public final class FabricAdapter implements IWildcardAdapter {

    private final ILogAssist assist = new FabricLogAssist();
    private final FabricComponentAdapter componentAdapter = new FabricComponentAdapter();

    @Override
    public IPlatformComponentAdapter<?> getComponentAdapter() {
        return componentAdapter;
    }

    @Override
    public ILogAssist getLogAssist() {
        return assist;
    }

    @Override
    public String getServerName() {
        return Singleton.get(PluginSettings.class).getString("server.name", "Minecraft Server");
    }

}
