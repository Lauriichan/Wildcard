package me.lauriichan.minecraft.wildcard.core;

import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;

public interface IWildcardAdapter {

    String getServerName();

    ILogAssist getLogAssist();
    
    IPlatformComponentAdapter<?> getComponentAdapter();

}
