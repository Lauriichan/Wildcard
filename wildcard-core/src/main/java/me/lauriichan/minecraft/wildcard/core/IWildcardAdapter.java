package me.lauriichan.minecraft.wildcard.core;

import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;

public interface IWildcardAdapter {

    ILogAssist getLogAssist();
    
    IPlatformComponentAdapter<?> getComponentAdapter();

}
