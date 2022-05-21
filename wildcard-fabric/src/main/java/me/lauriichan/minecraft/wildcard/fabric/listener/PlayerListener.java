package me.lauriichan.minecraft.wildcard.fabric.listener;

import com.mojang.authlib.GameProfile;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.data.storage.Database;
import me.lauriichan.minecraft.wildcard.core.listener.ConnectionListener;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.settings.Translation;
import me.lauriichan.minecraft.wildcard.mixin.api.FabricMixin;
import me.lauriichan.minecraft.wildcard.mixin.api.IPlayerJoinCallback;
import net.minecraft.text.MutableText;

public class PlayerListener extends ConnectionListener implements IPlayerJoinCallback {

    public static final Container<PlayerListener> LISTENER = Container.of();

    private final WildcardCore core;
    private final IWildcardAdapter adapter;
    private final IPlatformComponentAdapter<MutableText> componentAdapter;

    @SuppressWarnings("unchecked")
    public PlayerListener(final WildcardCore core, final Container<Database> database) {
        super(database);
        if (LISTENER.isPresent()) {
            throw new IllegalStateException("PlayerListener is already initialized");
        }
        this.core = core;
        this.adapter = core.getPlugin().getAdapter();
        this.componentAdapter = (IPlatformComponentAdapter<MutableText>) adapter.getComponentAdapter();
        LISTENER.replace(this).lock();
        FabricMixin.JOIN_CALLBACK.replace(this).lock();
    }

    @Override
    public void onJoin(Container<MutableText> container, GameProfile profile) {
        getDatabase().isAllowed(profile.getId()).thenAccept(allowed -> {
            if (allowed) {
                return;
            }
            container.replace(componentAdapter.asHandle(Translation.getDefault().translateComponent(core.getComponentParser(),
                "unpermitted.join", "server", core.getServerName()))[0]);
        }).join();
    }

}