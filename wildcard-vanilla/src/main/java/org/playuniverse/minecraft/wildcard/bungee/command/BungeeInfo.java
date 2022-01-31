package org.playuniverse.minecraft.wildcard.bungee.command;

import java.util.Optional;
import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.WildcardCore;
import org.playuniverse.minecraft.wildcard.core.command.api.base.BaseInfo;
import org.playuniverse.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import org.playuniverse.minecraft.wildcard.core.message.PlatformComponent;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeInfo extends BaseInfo {

    private final CommandSender sender;
    private final IPlatformComponentAdapter<BaseComponent> adapter;

    @SuppressWarnings("unchecked")
    public BungeeInfo(final WildcardCore core, final CommandSender sender) {
        super(core);
        this.sender = sender;
        this.adapter = (IPlatformComponentAdapter<BaseComponent>) parser.getAdapter();
    }

    public CommandSender getSender() {
        return sender;
    }

    @Override
    public boolean isPlayer() {
        return isSender(ProxiedPlayer.class);
    }

    @Override
    public UUID getSenderId() {
        return getSender(ProxiedPlayer.class).map(ProxiedPlayer::getUniqueId).orElse(WildcardCore.SERVER_UID);
    }

    public boolean isSender(final Class<? extends CommandSender> clazz) {
        return clazz.isAssignableFrom(sender.getClass());
    }

    public <T extends CommandSender> Optional<T> getSender(final Class<T> clazz) {
        return Optional.ofNullable(isSender(clazz) ? clazz.cast(sender) : null);
    }

    @Override
    public void send(final PlatformComponent[] message) {
        sender.sendMessage(adapter.asHandle(message));
    }

    @Override
    public boolean isPermitted(final String permission) {
        return sender.hasPermission(permission);
    }

}
