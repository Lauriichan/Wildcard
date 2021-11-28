package org.playuniverse.minecraft.wildcard.bungee;

import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.MessageAdapter;
import org.playuniverse.minecraft.wildcard.core.util.ComponentParser;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class BungeeSender extends MessageAdapter {

    private ProxiedPlayer player;

    public BungeeSender(final ComponentParser parser, final UUID uniqueId) {
        super(parser, uniqueId);
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null && player.isConnected();
    }

    public ProxiedPlayer getPlayer() {
        if (player != null && player.isConnected()) {
            return player;
        }
        return player = ProxyServer.getInstance().getPlayer(uniqueId);
    }

    @Override
    public void send(final TextComponent[] message) {
        final ProxiedPlayer player = getPlayer();
        if (player == null) {
            return;
        }
        player.sendMessage(message);
    }

    @Override
    public void kick(final TextComponent[] message) {
        final ProxiedPlayer player = getPlayer();
        if (player == null) {
            return;
        }
        player.disconnect(message);
    }

}
