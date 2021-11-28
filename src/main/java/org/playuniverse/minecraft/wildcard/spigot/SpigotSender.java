package org.playuniverse.minecraft.wildcard.spigot;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.wildcard.core.MessageAdapter;
import org.playuniverse.minecraft.wildcard.core.util.ComponentParser;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public final class SpigotSender extends MessageAdapter {

    private Player player;

    public SpigotSender(final ComponentParser parser, final UUID uniqueId) {
        super(parser, uniqueId);
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
    public void send(final TextComponent[] message) {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }
        player.spigot().sendMessage(message);
    }

    @Override
    public void kick(final TextComponent[] message) {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }
        player.kickPlayer(BaseComponent.toLegacyText(message));
    }

}
