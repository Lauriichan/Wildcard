package me.lauriichan.minecraft.wildcard.sponge.listener;

import java.util.UUID;

import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.data.storage.Database;
import me.lauriichan.minecraft.wildcard.core.listener.ConnectionListener;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.settings.Translation;
import net.kyori.adventure.text.Component;

public class PlayerListener extends ConnectionListener {

    private final WildcardCore core;
    private final IWildcardAdapter adapter;
    private final IPlatformComponentAdapter<Component> componentAdapter;

    @SuppressWarnings("unchecked")
    public PlayerListener(WildcardCore core, Container<Database> database) {
        super(database);
        this.core = core;
        this.adapter = core.getPlugin().getAdapter();
        this.componentAdapter = (IPlatformComponentAdapter<Component>) adapter.getComponentAdapter();
    }

    public void onPlayerJoin(ServerSideConnectionEvent.Handshake event) {
        final UUID uniqueId = event.profile().uuid();
        if (uniqueId == null) {
            return;
        }
        getDatabase().isAllowed(uniqueId).thenAccept(allowed -> {
            if (allowed) {
                return;
            }
            event.connection().close(componentAdapter.asHandle(Translation.getDefault().translateComponent(core.getComponentParser(),
                "unpermitted.join", "server", adapter.getServerName()))[0]);
        }).join();
    }

}
