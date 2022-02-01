package me.lauriichan.minecraft.wildcard.forge.listener;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.data.storage.Database;
import me.lauriichan.minecraft.wildcard.core.listener.ConnectionListener;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.settings.Translation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerListener extends ConnectionListener {

    public static final Container<PlayerListener> LISTENER = Container.of();

    private final WildcardCore core;
    private final IWildcardAdapter adapter;
    private final IPlatformComponentAdapter<ITextComponent> componentAdapter;

    @SuppressWarnings("unchecked")
    public PlayerListener(final WildcardCore core, final Container<Database> database) {
        super(database);
        if (LISTENER.isPresent()) {
            throw new IllegalStateException("PlayerListener is already initialized");
        }
        this.core = core;
        this.adapter = core.getPlugin().getAdapter();
        this.componentAdapter = (IPlatformComponentAdapter<ITextComponent>) adapter.getComponentAdapter();

        LISTENER.replace(this).lock();
    }
    
    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) { // To prevent Vanilla players
        PlayerEntity entity = event.getPlayer();
        if (!(entity instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        getDatabase().isAllowed(player.getUUID()).thenAccept(allowed -> {
            if (allowed) {
                return;
            }
            player.connection.disconnect(componentAdapter.asHandle(Translation.getDefault().translateComponent(core.getComponentParser(),
                "unpermitted.join", "server", adapter.getServerName()))[0]);
            event.setResult(Result.DENY);
        }).join();
    }

}
