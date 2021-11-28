package org.playuniverse.minecraft.wildcard.spigot.listener;

import java.util.UUID;

import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.playuniverse.minecraft.wildcard.core.IWildcardAdapter;
import org.playuniverse.minecraft.wildcard.core.data.storage.Database;
import org.playuniverse.minecraft.wildcard.core.listener.ConnectionListener;
import org.playuniverse.minecraft.wildcard.core.settings.Translation;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.event.EventHandler;

public final class PlayerListener extends ConnectionListener implements Listener {

    private final IWildcardAdapter adapter;

    public PlayerListener(final IWildcardAdapter adapter, final Container<Database> database) {
        super(database);
        this.adapter = adapter;
    }

    @EventHandler
    public void onLogin(final AsyncPlayerPreLoginEvent event) {
        final UUID uniqueId = event.getUniqueId();
        if (uniqueId == null) {
            return;
        }
        getDatabase().isAllowed(uniqueId).thenAccept(allowed -> {
            if (allowed) {
                return;
            }
            event.disallow(Result.KICK_OTHER, BaseComponent.toLegacyText(Translation.getDefault()
                .translateComponent(adapter.getComponentParser(), "unpermitted.join", "server", adapter.getServerName())));
        }).join();
    }

}
