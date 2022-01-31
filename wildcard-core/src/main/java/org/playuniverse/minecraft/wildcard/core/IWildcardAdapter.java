package org.playuniverse.minecraft.wildcard.core;

import org.playuniverse.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import org.playuniverse.minecraft.wildcard.core.util.ILogAssist;

public interface IWildcardAdapter {

    String getServerName();

    ILogAssist getLogAssist();
    
    IPlatformComponentAdapter<?> getComponentAdapter();

}
