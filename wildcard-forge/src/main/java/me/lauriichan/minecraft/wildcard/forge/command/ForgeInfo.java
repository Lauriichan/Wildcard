package me.lauriichan.minecraft.wildcard.forge.command;

import java.util.Optional;
import java.util.UUID;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseInfo;
import me.lauriichan.minecraft.wildcard.core.command.api.base.IPermission;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;

public class ForgeInfo extends BaseInfo {

    private final CommandSource sender;
    private final IPlatformComponentAdapter<ITextComponent> adapter;

    @SuppressWarnings("unchecked")
    public ForgeInfo(final WildcardCore core, final CommandSource sender) {
        super(core);
        this.sender = sender;
        this.adapter = (IPlatformComponentAdapter<ITextComponent>) parser.getAdapter();
    }

    public CommandSource getSender() {
        return sender;
    }

    @Override
    public boolean isPlayer() {
        try {
            sender.getPlayerOrException();
            return true;
        } catch (CommandSyntaxException ig) {
            return false;
        }
    }

    private ServerPlayerEntity getPlayer() {
        try {
            return sender.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            // Not possible tho
            return null;
        }
    }

    @Override
    public UUID getSenderId() {
        if (isPlayer()) {
            return getPlayer().getUUID();
        }
        return WildcardCore.SERVER_UID;
    }

    public boolean isSender(final Class<? extends ICommandSource> clazz) {
        return false;
    }

    public <T extends ICommandSource> Optional<T> getSender(final Class<T> clazz) {
        return Optional.ofNullable(isSender(clazz) ? clazz.cast(sender) : null);
    }

    @Override
    public void send(final PlatformComponent[] message) {
        sender.sendSuccess(adapter.asHandle(message)[0], true);
    }

    @Override
    public boolean isPermitted(final String permission) {
        return hasPermission(2);
    }

    public boolean isPermitted(IPermission permission) {
        return hasPermission(permission.level());
    }

    private boolean hasPermission(int level) {
        if (!isPlayer()) {
            return true;
        }
        return getPlayer().hasPermissions(level);
    }

}
