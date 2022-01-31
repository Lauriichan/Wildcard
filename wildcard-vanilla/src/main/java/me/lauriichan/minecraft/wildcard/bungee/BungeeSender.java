package me.lauriichan.minecraft.wildcard.bungee;

import java.util.UUID;

import me.lauriichan.minecraft.wildcard.core.MessageAdapter;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponentParser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class BungeeSender extends MessageAdapter {

    private ProxiedPlayer player;
    private final IPlatformComponentAdapter<BaseComponent> adapter;

    @SuppressWarnings("unchecked")
    public BungeeSender(final PlatformComponentParser parser, final UUID uniqueId) {
        super(parser, uniqueId);
        this.adapter = (IPlatformComponentAdapter<BaseComponent>) parser.getAdapter();
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
    public void send(final PlatformComponent[] message) {
        final ProxiedPlayer player = getPlayer();
        if (player == null) {
            return;
        }
        player.sendMessage(adapter.asHandle(message));
    }

    @Override
    public void kick(final PlatformComponent[] message) {
        final ProxiedPlayer player = getPlayer();
        if (player == null) {
            return;
        }
        player.disconnect(adapter.asHandle(message));
    }

}
