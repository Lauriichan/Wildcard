package me.lauriichan.minecraft.wildcard.spigot.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.data.storage.Database;
import me.lauriichan.minecraft.wildcard.core.listener.ConnectionListener;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.settings.Translation;
import net.md_5.bungee.api.chat.BaseComponent;

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
    public void onLogin(final AsyncPlayerPreLoginEvent event) {
        final UUID uniqueId = event.getUniqueId();
        if (uniqueId == null) {
            return;
        }
        getDatabase().isAllowed(uniqueId).thenAccept(allowed -> {
            if (allowed) {
                return;
            }
            event.disallow(Result.KICK_OTHER, BaseComponent.toLegacyText(componentAdapter.asHandle(Translation.getDefault()
                .translateComponent(core.getComponentParser(), "unpermitted.join", "server", core.getServerName()))));
        }).join();
    }

}
