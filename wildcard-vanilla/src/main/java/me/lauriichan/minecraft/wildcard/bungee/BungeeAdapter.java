package me.lauriichan.minecraft.wildcard.bungee;

import java.awt.Color;

import me.lauriichan.minecraft.wildcard.bungee.component.BungeeComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public final class BungeeAdapter implements IWildcardAdapter {

    private final BungeeComponentAdapter componentAdapter = new BungeeComponentAdapter(this);
    private final ILogAssist assist = new BungeeLogAssist();
    
    @Override
    public BungeeComponentAdapter getComponentAdapter() {
        return componentAdapter;
    }

    @Override
    public ILogAssist getLogAssist() {
        return assist;
    }
    
    public void applyColor(final TextComponent component, final Color color) {
        component.setColor(ChatColor.of(color));
    }

}
