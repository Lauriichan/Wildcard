package org.playuniverse.minecraft.wildcard.spigot.command;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.playuniverse.minecraft.wildcard.core.WildcardCore;
import org.playuniverse.minecraft.wildcard.core.command.api.base.BaseInfo;

import net.md_5.bungee.api.chat.TextComponent;

public class SpigotInfo extends BaseInfo {

    private final CommandSender sender;

    public SpigotInfo(final WildcardCore core, final CommandSender sender) {
        super(core);
        this.sender = sender;
    }

    public CommandSender getSender() {
        return sender;
    }

    @Override
    public boolean isPlayer() {
        return isSender(Player.class);
    }

    @Override
    public UUID getSenderId() {
        return getSender(Player.class).map(Player::getUniqueId).orElse(WildcardCore.SERVER_UID);
    }

    public boolean isSender(final Class<? extends CommandSender> clazz) {
        return clazz.isAssignableFrom(sender.getClass());
    }

    public <T extends CommandSender> Optional<T> getSender(final Class<T> clazz) {
        return Optional.ofNullable(isSender(clazz) ? clazz.cast(sender) : null);
    }

    @Override
    public void send(final TextComponent[] message) {
        sender.spigot().sendMessage(message);
    }

    @Override
    public boolean isPermitted(final String permission) {
        return sender.hasPermission(permission);
    }

}
