package me.lauriichan.minecraft.wildcard.fabric;

import java.lang.ref.WeakReference;
import java.util.UUID;

import me.lauriichan.minecraft.wildcard.core.MessageAdapter;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponentParser;
import me.lauriichan.minecraft.wildcard.mixin.api.FabricMixin;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

public final class FabricSender extends MessageAdapter {

    private WeakReference<ServerPlayerEntity> playerRef;
    private final IPlatformComponentAdapter<MutableText> adapter;

    @SuppressWarnings("unchecked")
    public FabricSender(PlatformComponentParser parser, UUID uniqueId) {
        super(parser, uniqueId);
        this.adapter = (IPlatformComponentAdapter<MutableText>) parser.getAdapter();
    }

    public ServerPlayerEntity getPlayer() {
        if (playerRef != null) {
            ServerPlayerEntity player = playerRef.get();
            if (player != null && !player.isDisconnected()) {
                return player;
            }
            playerRef = null; // Remove player reference
        }
        ServerPlayerEntity player = FabricMixin.server().getPlayerManager().getPlayer(uniqueId);
        if (player != null) {
            playerRef = new WeakReference<>(player);
        }
        return player;
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null;
    }

    @Override
    public void kick(PlatformComponent[] message) {
        ServerPlayerEntity player = getPlayer();
        if (player == null) {
            return;
        }
        player.networkHandler.disconnect(adapter.asHandle(message)[0]);
    }

    @Override
    public void send(PlatformComponent[] message) {
        ServerPlayerEntity player = getPlayer();
        if (player == null) {
            return;
        }
        player.sendSystemMessage(adapter.asHandle(message)[0], null);
    }

}
