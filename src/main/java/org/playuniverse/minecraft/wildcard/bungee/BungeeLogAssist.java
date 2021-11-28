package org.playuniverse.minecraft.wildcard.bungee;

import org.playuniverse.minecraft.wildcard.core.util.ILogAssist;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

final class BungeeLogAssist implements ILogAssist {

    private final CommandSender console = ProxyServer.getInstance().getConsole();

    @Override
    public void info(final String message) {
        console.sendMessage(TextComponent.fromLegacyText(message));
    }

}
