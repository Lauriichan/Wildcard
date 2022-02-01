package me.lauriichan.minecraft.wildcard.forge;

import java.lang.ref.WeakReference;
import java.util.UUID;

import me.lauriichan.minecraft.wildcard.core.MessageAdapter;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponentParser;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public final class ForgeSender extends MessageAdapter {
    
    private WeakReference<ServerPlayerEntity> playerRef;
    private final IPlatformComponentAdapter<ITextComponent> adapter;

    @SuppressWarnings("unchecked")
    public ForgeSender(PlatformComponentParser parser, UUID uniqueId) {
        super(parser, uniqueId);
        this.adapter = (IPlatformComponentAdapter<ITextComponent>) parser.getAdapter();
    }

    public ServerPlayerEntity getPlayer() {
        if (playerRef != null) {
            ServerPlayerEntity player = playerRef.get();
            if (player != null && !player.hasDisconnected()) {
                return player;
            }
            playerRef = null; // Remove player reference
        }
        ServerPlayerEntity player = ((MinecraftServer) LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER)).getPlayerList().getPlayer(uniqueId);
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
        if(player == null) {
            return;
        }
        player.connection.disconnect(adapter.asHandle(message)[0]);
    }

    @Override
    public void send(PlatformComponent[] message) {
        ServerPlayerEntity player = getPlayer();
        if(player == null) {
            return;
        }
        player.sendMessage(adapter.asHandle(message)[0], null);
    }

}
