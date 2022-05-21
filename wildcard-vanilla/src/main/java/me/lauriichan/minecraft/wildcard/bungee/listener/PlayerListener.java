package me.lauriichan.minecraft.wildcard.bungee.listener;

import java.util.UUID;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.data.storage.Database;
import me.lauriichan.minecraft.wildcard.core.listener.ConnectionListener;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.settings.Translation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class PlayerListener extends ConnectionListener implements Listener {

    private final WildcardCore core;
    private final IWildcardAdapter adapter;
    private final IPlatformComponentAdapter<BaseComponent> componentAdapter;

    @SuppressWarnings("unchecked")
    public PlayerListener(final WildcardCore core, final Container<Database> database) {
        super(database);
        this.core = core;
        this.adapter = core.getPlugin().getAdapter();
        this.componentAdapter = (IPlatformComponentAdapter<BaseComponent>) adapter.getComponentAdapter();
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
            player.disconnect(componentAdapter.asHandle(Translation.getDefault().translateComponent(core.getComponentParser(),
                "unpermitted.join", "server", core.getServerName())));
        }).join();
    }

}
