package me.lauriichan.minecraft.wildcard.sponge.command;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseInfo;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;

public class SpongeInfo extends BaseInfo {

    private final CommandCause sender;
    private final IPlatformComponentAdapter<Component> adapter;

    @SuppressWarnings("unchecked")
    public SpongeInfo(final WildcardCore core, final CommandCause sender) {
        super(core);
        this.sender = sender;
        this.adapter = (IPlatformComponentAdapter<Component>) parser.getAdapter();
    }

    public CommandCause getSender() {
        return sender;
    }

    @Override
    public boolean isPlayer() {
        return isSender(ServerPlayer.class);
    }

    @Override
    public UUID getSenderId() {
        return getSender(ServerPlayer.class).map(ServerPlayer::uniqueId).orElse(WildcardCore.SERVER_UID);
    }

    public boolean isSender(final Class<? extends Subject> clazz) {
        return clazz.isAssignableFrom(sender.getClass());
    }

    public <T extends Subject> Optional<T> getSender(final Class<T> clazz) {
        return Optional.ofNullable(isSender(clazz) ? clazz.cast(sender) : null);
    }

    @Override
    public void send(final PlatformComponent[] message) {
        sender.sendMessage(Identity.nil(), adapter.asHandle(message)[0]);
    }

    @Override
    public boolean isPermitted(final String permission) {
        return sender.hasPermission(permission);
    }

}
