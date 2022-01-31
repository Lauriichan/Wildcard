package org.playuniverse.minecraft.wildcard.spigot;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.wildcard.core.MessageAdapter;
import org.playuniverse.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import org.playuniverse.minecraft.wildcard.core.message.PlatformComponent;
import org.playuniverse.minecraft.wildcard.core.message.PlatformComponentParser;

import net.md_5.bungee.api.chat.BaseComponent;

public final class SpigotSender extends MessageAdapter {

    private Player player;
    private final IPlatformComponentAdapter<BaseComponent> adapter;

    @SuppressWarnings("unchecked")
    public SpigotSender(final PlatformComponentParser parser, final UUID uniqueId) {
        super(parser, uniqueId);
        this.adapter = (IPlatformComponentAdapter<BaseComponent>) parser.getAdapter();
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null && player.isOnline();
    }

    public Player getPlayer() {
        if (player != null && player.isOnline()) {
            return player;
        }
        return player = Bukkit.getPlayer(uniqueId);
    }

    @Override
    public void send(final PlatformComponent[] message) {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }
        player.spigot().sendMessage(adapter.asHandle(message));
    }

    @Override
    public void kick(final PlatformComponent[] message) {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }
        player.kickPlayer(BaseComponent.toLegacyText(adapter.asHandle(message)));
    }

}
