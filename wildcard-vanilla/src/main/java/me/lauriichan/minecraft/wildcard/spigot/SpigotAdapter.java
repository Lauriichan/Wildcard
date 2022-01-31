package me.lauriichan.minecraft.wildcard.spigot;

import java.awt.Color;

import org.bukkit.Bukkit;

import com.syntaxphoenix.syntaxapi.version.DefaultVersion;
import com.syntaxphoenix.syntaxapi.version.Version;

import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;
import me.lauriichan.minecraft.wildcard.spigot.adapter.SpigotAdapter1_16;
import me.lauriichan.minecraft.wildcard.spigot.adapter.SpigotAdapter1_8;
import me.lauriichan.minecraft.wildcard.spigot.component.SpigotComponentAdapter;
import net.md_5.bungee.api.chat.TextComponent;

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
    
    protected final ILogAssist assist = new SpigotLogAssist();
    protected final SpigotComponentAdapter componentAdapter = new SpigotComponentAdapter(this);

    @Override
    public ILogAssist getLogAssist() {
        return assist;
    }
    
    @Override
    public SpigotComponentAdapter getComponentAdapter() {
        return componentAdapter;
    }
    
    public abstract void applyColor(TextComponent component, Color color);

}
