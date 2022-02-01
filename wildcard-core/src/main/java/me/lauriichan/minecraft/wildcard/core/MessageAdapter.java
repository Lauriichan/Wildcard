package me.lauriichan.minecraft.wildcard.core;

import java.util.UUID;

import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponentParser;
import me.lauriichan.minecraft.wildcard.core.settings.Translation;

public abstract class MessageAdapter {

    protected final PlatformComponentParser parser;
    protected final UUID uniqueId;

    public MessageAdapter(final PlatformComponentParser parser, final UUID uniqueId) {
        this.parser = parser;
        this.uniqueId = uniqueId;
    }

    public final UUID getUniqueId() {
        return uniqueId;
    }

    public abstract boolean isOnline();

    public abstract void send(PlatformComponent[] message);

    public final void send(final String id) {
        send(Translation.getDefault().translateComponent(parser, id));
    }

    public final void send(final String id, final Object... placeholders) {
        send(Translation.getDefault().translateComponent(parser, id, placeholders));
    }

    public abstract void kick(PlatformComponent[] message);

}
