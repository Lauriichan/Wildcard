package me.lauriichan.minecraft.wildcard.sponge.command;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseInfo;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;

public class SpongeInfo extends BaseInfo {

    private final CommandSource sender;
    private final IPlatformComponentAdapter<Text> adapter;

    @SuppressWarnings("unchecked")
    public SpongeInfo(final WildcardCore core, final CommandSource sender) {
        super(core);
        this.sender = sender;
        this.adapter = (IPlatformComponentAdapter<Text>) parser.getAdapter();
    }

    public CommandSource getSender() {
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

    public boolean isSender(final Class<? extends Subject> clazz) {
        return clazz.isAssignableFrom(sender.getClass());
    }

    public <T extends Subject> Optional<T> getSender(final Class<T> clazz) {
        return Optional.ofNullable(isSender(clazz) ? clazz.cast(sender) : null);
    }

    @Override
    public void send(final PlatformComponent[] message) {
        sender.sendMessage(adapter.asHandle(message)[0]);
    }

    @Override
    public boolean isPermitted(final String permission) {
        return sender.hasPermission(permission);
    }

}
