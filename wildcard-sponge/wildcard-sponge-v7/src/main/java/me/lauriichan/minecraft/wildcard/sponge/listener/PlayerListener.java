package me.lauriichan.minecraft.wildcard.sponge.listener;

import java.util.UUID;

import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.data.storage.Database;
import me.lauriichan.minecraft.wildcard.core.listener.ConnectionListener;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.settings.Translation;

public class PlayerListener extends ConnectionListener {

    private final WildcardCore core;
    private final IWildcardAdapter adapter;
    private final IPlatformComponentAdapter<Text> componentAdapter;

    @SuppressWarnings("unchecked")
    public PlayerListener(WildcardCore core, Container<Database> database) {
        super(database);
        this.core = core;
        this.adapter = core.getPlugin().getAdapter();
        this.componentAdapter = (IPlatformComponentAdapter<Text>) adapter.getComponentAdapter();
    }

    public void onPlayerJoin(ClientConnectionEvent.Auth event) {
        final UUID uniqueId = event.getProfile().getUniqueId();
        if (uniqueId == null) {
            return;
        }
        getDatabase().isAllowed(uniqueId).thenAccept(allowed -> {
            if (allowed) {
                return;
            }
            event.setCancelled(true);
            event.setMessage(componentAdapter.asHandle(Translation.getDefault().translateComponent(core.getComponentParser(),
                "unpermitted.join", "server", adapter.getServerName()))[0]);
        }).join();
    }

}
