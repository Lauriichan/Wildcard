package org.playuniverse.minecraft.wildcard.bungee;

import java.awt.Color;

import org.playuniverse.minecraft.wildcard.core.IWildcardAdapter;
import org.playuniverse.minecraft.wildcard.core.util.ComponentParser;
import org.playuniverse.minecraft.wildcard.core.util.ILogAssist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public final class BungeeAdapter implements IWildcardAdapter {

    private final ComponentParser componentParser = new ComponentParser(this);
    private final ILogAssist assist = new BungeeLogAssist();

    @Override
    public ComponentParser getComponentParser() {
        return componentParser;
    }

    @Override
    public String getServerName() {
        return ProxyServer.getInstance().getName();
    }

    @Override
    public String transformColor(final Color color) {
        return ChatColor.of(color).toString();
    }

    @Override
    public void applyColor(final TextComponent component, final Color color) {
        component.setColor(ChatColor.of(color));
    }

    @Override
    public ILogAssist getLogAssist() {
        return assist;
    }

}
