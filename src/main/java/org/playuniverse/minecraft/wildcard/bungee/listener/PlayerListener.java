package org.playuniverse.minecraft.wildcard.bungee.listener;

import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.IWildcardAdapter;
import org.playuniverse.minecraft.wildcard.core.data.storage.Database;
import org.playuniverse.minecraft.wildcard.core.listener.ConnectionListener;
import org.playuniverse.minecraft.wildcard.core.settings.Translation;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class PlayerListener extends ConnectionListener implements Listener {

    private final IWildcardAdapter adapter;

    public PlayerListener(final IWildcardAdapter adapter, final Container<Database> database) {
        super(database);
        this.adapter = adapter;
    }

    @EventHandler
    public void onPostLogin(final PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final UUID uniqueId = player.getUniqueId();
        if (uniqueId == null) {
            return;
        }
        getDatabase().isAllowed(uniqueId).thenAccept(allowed -> {
            if (allowed) {
                return;
            }
            player.disconnect(Translation.getDefault().translateComponent(adapter.getComponentParser(), "unpermitted.join", "server",
                adapter.getServerName()));
        }).join();
    }

}
