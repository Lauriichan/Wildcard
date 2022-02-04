package me.lauriichan.minecraft.wildcard.core;

import com.syntaxphoenix.syntaxapi.logging.ILogger;

import me.lauriichan.minecraft.wildcard.core.settings.PluginSettings;
import me.lauriichan.minecraft.wildcard.core.util.Singleton;

public final class Wildcard {
    
    private Wildcard() {
        throw new UnsupportedOperationException("Constant provider");
    }

    public static boolean isDebug() {
        return Singleton.get(PluginSettings.class).getBoolean("debug", false);
    }
    
    public static ILogger getLogger() {
        return Singleton.get(ILogger.class);
    }

}
