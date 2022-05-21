package me.lauriichan.minecraft.wildcard.spigot.adapter;

import java.awt.Color;

import me.lauriichan.minecraft.wildcard.spigot.SpigotAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public final class SpigotAdapter1_16 extends SpigotAdapter {

    @Override
    public void applyColor(final TextComponent component, final Color color) {
        component.setColor(ChatColor.of(color));
    }

}
