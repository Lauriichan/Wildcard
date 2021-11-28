package org.playuniverse.minecraft.wildcard.core.command.api.base;

import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.IWildcardAdapter;
import org.playuniverse.minecraft.wildcard.core.IWildcardPlugin;
import org.playuniverse.minecraft.wildcard.core.MessageAdapter;
import org.playuniverse.minecraft.wildcard.core.ServiceAdapter;
import org.playuniverse.minecraft.wildcard.core.WildcardCore;
import org.playuniverse.minecraft.wildcard.core.data.storage.Database;
import org.playuniverse.minecraft.wildcard.core.settings.Translation;
import org.playuniverse.minecraft.wildcard.core.util.ComponentParser;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class BaseInfo {

    private final WildcardCore core;
    private final ComponentParser parser;

    public BaseInfo(final WildcardCore core) {
        this.core = core;
        this.parser = core.getPlugin().getAdapter().getComponentParser();
    }

    public boolean isPlayer() {
        return false;
    }

    public String getSenderName() {
        return getName(getSenderId());
    }

    public UUID getSenderId() {
        return WildcardCore.SERVER_UID;
    }

    public MessageAdapter getSenderAdapter() {
        return getMessageAdapter(getSenderId());
    }

    public final ILogger getLogger() {
        return core.getLogger();
    }

    public final ComponentParser getParser() {
        return parser;
    }

    public final WildcardCore getCore() {
        return core;
    }

    public final IWildcardPlugin getPlugin() {
        return core.getPlugin();
    }

    public final IWildcardAdapter getAdapter() {
        return core.getPlugin().getAdapter();
    }

    public final ServiceAdapter getService() {
        return core.getPlugin().getService();
    }

    public final Container<Database> getDatabase() {
        return core.getDatabase();
    }

    public final String getName(final UUID uniqueId) {
        return uniqueId == WildcardCore.SERVER_UID ? getAdapter().getServerName() : getService().getName(uniqueId);
    }

    public final UUID getUniqueId(final String name) {
        return getService().getUniqueId(name);
    }

    public final MessageAdapter getMessageAdapter(final String name) {
        return getMessageAdapter(getUniqueId(name));
    }

    public final MessageAdapter getMessageAdapter(final UUID uniqueId) {
        return core.getPlugin().getService().getMessageAdapter(uniqueId);
    }

    public final Translation getTranslation() {
        return Translation.getDefault();
    }

    public void send(final TextComponent[] message) {}

    public final void send(final String id) {
        send(Translation.getDefault().translateComponent(parser, id));
    }

    public final void send(final String id, final Object... placeholders) {
        send(Translation.getDefault().translateComponent(parser, id, placeholders));
    }

    public final TextComponent[] translate(final String id) {
        return Translation.getDefault().translateComponent(parser, id);
    }

    public final TextComponent[] translate(final String id, final Object... placeholders) {
        return Translation.getDefault().translateComponent(parser, id, placeholders);
    }

    public final void apply(final TextComponent[] components, final ClickEvent event) {
        apply(components, event, null);
    }

    public final void apply(final TextComponent[] components, final HoverEvent event) {
        apply(components, null, event);
    }

    public final void apply(final TextComponent[] components, final ClickEvent click, final HoverEvent hover) {
        for (final TextComponent component : components) {
            if (click != null) {
                component.setClickEvent(click);
            }
            if (hover != null) {
                component.setHoverEvent(hover);
            }
        }
    }

    public boolean isPermitted(final String permission) {
        return false;
    }

    public final boolean isPermitted(final IPermission permission) {
        return isPermitted(permission.id());
    }

}
