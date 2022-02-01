package me.lauriichan.minecraft.wildcard.forge;

import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;
import me.lauriichan.minecraft.wildcard.forge.component.ForgeComponentAdapter;

public final class ForgeAdapter implements IWildcardAdapter {

    private final ILogAssist assist = new ForgeLogAssist();
    private final ForgeComponentAdapter componentAdapter = new ForgeComponentAdapter();

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
        return ForgeConfiguration.SERVER.serverName.get();
    }

}
