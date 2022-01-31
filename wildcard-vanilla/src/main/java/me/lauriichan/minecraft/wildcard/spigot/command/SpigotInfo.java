package me.lauriichan.minecraft.wildcard.spigot.command;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseInfo;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import net.md_5.bungee.api.chat.BaseComponent;

public class SpigotInfo extends BaseInfo {

    private final CommandSender sender;
    private final IPlatformComponentAdapter<BaseComponent> adapter;

    @SuppressWarnings("unchecked")
    public SpigotInfo(final WildcardCore core, final CommandSender sender) {
        super(core);
        this.sender = sender;
        this.adapter = (IPlatformComponentAdapter<BaseComponent>) parser.getAdapter();
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
    public void send(final PlatformComponent[] message) {
        sender.spigot().sendMessage(adapter.asHandle(message));
    }

    @Override
    public boolean isPermitted(final String permission) {
        return sender.hasPermission(permission);
    }

}
