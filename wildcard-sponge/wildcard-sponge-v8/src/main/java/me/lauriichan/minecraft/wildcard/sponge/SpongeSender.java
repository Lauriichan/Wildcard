package me.lauriichan.minecraft.wildcard.sponge;

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import me.lauriichan.minecraft.wildcard.core.MessageAdapter;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponentParser;
import net.kyori.adventure.text.Component;

public class SpongeSender extends MessageAdapter {

    private WeakReference<ServerPlayer> playerRef;
    private final IPlatformComponentAdapter<Component> adapter;

    @SuppressWarnings("unchecked")
    public SpongeSender(PlatformComponentParser parser, UUID uniqueId) {
        super(parser, uniqueId);
        this.adapter = (IPlatformComponentAdapter<Component>) parser.getAdapter();
    }

    public ServerPlayer getPlayer() {
        if (playerRef != null) {
            ServerPlayer player = playerRef.get();
            if (player != null && player.isOnline()) {
                return player;
            }
            playerRef = null; // Remove player reference
        }
        ServerPlayer player = Sponge.server().player(uniqueId).orElse(null);
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
    public void send(PlatformComponent[] message) {
        ServerPlayer player = getPlayer();
        if (player == null) {
            return;
        }
        player.sendMessage(adapter.asHandle(message)[0]);
    }

    @Override
    public void kick(PlatformComponent[] message) {
        ServerPlayer player = getPlayer();
        if (player == null) {
            return;
        }
        player.kick(adapter.asHandle(message)[0]);
    }

}
