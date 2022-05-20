package me.lauriichan.minecraft.wildcard.sponge;

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import me.lauriichan.minecraft.wildcard.core.MessageAdapter;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponentParser;

public class SpongeSender extends MessageAdapter {

    private WeakReference<Player> playerRef;
    private final IPlatformComponentAdapter<Text> adapter;

    @SuppressWarnings("unchecked")
    public SpongeSender(PlatformComponentParser parser, UUID uniqueId) {
        super(parser, uniqueId);
        this.adapter = (IPlatformComponentAdapter<Text>) parser.getAdapter();
    }

    public Player getPlayer() {
        if (playerRef != null) {
            Player player = playerRef.get();
            if (player != null && player.isOnline()) {
                return player;
            }
            playerRef = null; // Remove player reference
        }
        Player player = Sponge.getServer().getPlayer(uniqueId).orElse(null);
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
        Player player = getPlayer();
        if (player == null) {
            return;
        }
        player.sendMessage(adapter.asHandle(message)[0]);
    }

    @Override
    public void kick(PlatformComponent[] message) {
        Player player = getPlayer();
        if (player == null) {
            return;
        }
        player.kick(adapter.asHandle(message)[0]);
    }

}
