package org.playuniverse.minecraft.wildcard.core;

import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.settings.Translation;
import org.playuniverse.minecraft.wildcard.core.util.ComponentParser;

import net.md_5.bungee.api.chat.TextComponent;

public abstract class MessageAdapter {

    protected final ComponentParser parser;
    protected final UUID uniqueId;

    public MessageAdapter(final ComponentParser parser, final UUID uniqueId) {
        this.parser = parser;
        this.uniqueId = uniqueId;
    }

    public final UUID getUniqueId() {
        return uniqueId;
    }

    public abstract boolean isOnline();

    public abstract void send(TextComponent[] message);

    public final void send(final String id) {
        send(Translation.getDefault().translateComponent(parser, id));
    }

    public final void send(final String id, final Object... placeholders) {
        send(Translation.getDefault().translateComponent(parser, id, placeholders));
    }

    public abstract void kick(TextComponent[] message);

}
