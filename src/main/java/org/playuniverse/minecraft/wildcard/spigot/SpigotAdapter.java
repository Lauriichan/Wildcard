package org.playuniverse.minecraft.wildcard.spigot;

import org.bukkit.Bukkit;
import org.playuniverse.minecraft.wildcard.core.IWildcardAdapter;
import org.playuniverse.minecraft.wildcard.core.util.ComponentParser;
import org.playuniverse.minecraft.wildcard.core.util.ILogAssist;
import org.playuniverse.minecraft.wildcard.spigot.adapter.SpigotAdapter1_16;
import org.playuniverse.minecraft.wildcard.spigot.adapter.SpigotAdapter1_8;

import com.syntaxphoenix.syntaxapi.version.DefaultVersion;
import com.syntaxphoenix.syntaxapi.version.Version;

public abstract class SpigotAdapter implements IWildcardAdapter {

    private static Version VERSION;

    static SpigotAdapter build() {
        final int version = getVersion().getMinor();
        if (version >= 16) {
            return new SpigotAdapter1_16();
        }
        if (version >= 8) {
            return new SpigotAdapter1_8();
        }
        return null;
    }

    public static Version getVersion() {
        return VERSION != null ? VERSION
            : (VERSION = new DefaultVersion().getAnalyzer().analyze(Bukkit.getVersion().split(" ")[2].replace(")", "")));
    }

    protected final ComponentParser componentParser = new ComponentParser(this);
    protected final ILogAssist assist = new SpigotLogAssist();

    @Override
    public final ComponentParser getComponentParser() {
        return componentParser;
    }

    @Override
    public ILogAssist getLogAssist() {
        return assist;
    }

}
