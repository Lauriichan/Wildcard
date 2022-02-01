package me.lauriichan.minecraft.wildcard.fabric.command;

import java.util.UUID;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseInfo;
import me.lauriichan.minecraft.wildcard.core.command.api.base.IPermission;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricInfo extends BaseInfo {

    private final ServerCommandSource source;

    public FabricInfo(WildcardCore core, ServerCommandSource source) {
        super(core);
        this.source = source;
    }

    public ServerCommandSource getSender() {
        return source;
    }

    @Override
    public boolean isPlayer() {
        try {
            source.getPlayer();
            return true;
        } catch (CommandSyntaxException ig) {
            return false;
        }
    }

    private ServerPlayerEntity getPlayer() {
        try {
            return source.getPlayer();
        } catch (CommandSyntaxException e) {
            // Not possible tho
            return null;
        }
    }

    @Override
    public UUID getSenderId() {
        if (isPlayer()) {
            return getPlayer().getUuid();
        }
        return WildcardCore.SERVER_UID;
    }

    @Override
    public boolean isPermitted(String permission) {
        return hasPermission(2);
    }

    @Override
    public boolean isPermitted(IPermission permission) {
        return hasPermission(permission.level());
    }

    private boolean hasPermission(int level) {
        if (!isPlayer()) {
            return true;
        }
        return source.hasPermissionLevel(level);
    }

}
