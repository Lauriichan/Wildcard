package org.playuniverse.minecraft.wildcard.core;

import java.awt.Color;

import org.playuniverse.minecraft.wildcard.core.util.ComponentParser;
import org.playuniverse.minecraft.wildcard.core.util.ILogAssist;

import net.md_5.bungee.api.chat.TextComponent;

public interface IWildcardAdapter {

    String getServerName();

    ILogAssist getLogAssist();

    String transformColor(Color color);

    ComponentParser getComponentParser();

    void applyColor(TextComponent component, Color color);

}
