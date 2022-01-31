package me.lauriichan.minecraft.wildcard.core.web.util;

import java.util.HashMap;

import com.syntaxphoenix.syntaxapi.event.Event;
import com.syntaxphoenix.syntaxapi.net.http.NamedType;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;

import me.lauriichan.minecraft.wildcard.core.util.placeholder.Placeable;
import me.lauriichan.minecraft.wildcard.core.util.placeholder.Placeholder;
import me.lauriichan.minecraft.wildcard.core.util.placeholder.PlaceholderStore;
import me.lauriichan.minecraft.wildcard.core.util.placeholder.Template;
import me.lauriichan.minecraft.wildcard.core.util.placeholder.TemplateStore;
import me.lauriichan.minecraft.wildcard.core.web.WebSender;

public class PageInjectPlaceholderEvent extends Event implements TemplateStore, PlaceholderStore {

    private final HashMap<String, Placeholder> placeholders = new HashMap<>();
    private final HashMap<String, Template> templates = new HashMap<>();

    private final NamedType type;
    private final WebSender sender;
    private final ReceivedRequest data;

    public PageInjectPlaceholderEvent(final NamedType type, final WebSender sender, final ReceivedRequest data) {
        this.type = type;
        this.sender = sender;
        this.data = data;
    }

    public ReceivedRequest getData() {
        return data;
    }

    public WebSender getSender() {
        return sender;
    }

    public NamedType getType() {
        return type;
    }

    @Override
    public void setTemplate(final Template value) {
        if (templates.containsKey(value.getKey())) {
            return;
        }
        templates.put(value.getKey(), value);
    }

    @Override
    public Template getTemplate(final String key) {
        return templates.get(key);
    }

    public boolean hasTemplate(final String key) {
        return templates.containsKey(key);
    }

    @Override
    public Template[] templateArray() {
        return templates.values().toArray(Template[]::new);
    }

    @Override
    public void setPlaceholder(final Placeholder value) {
        if (placeholders.containsKey(value.getKey())) {
            return;
        }
        placeholders.put(value.getKey(), value);
    }

    @Override
    public Placeholder getPlaceholder(final String key) {
        return placeholders.get(key);
    }

    public boolean hasPlaceholder(final String key) {
        return placeholders.containsKey(key);
    }

    @Override
    public Placeholder[] placeholderArray() {
        return placeholders.values().toArray(Placeholder[]::new);
    }

    @Override
    public Placeable getPlaceable(final String key, final boolean flag) {
        return getPlaceholder(key);
    }

    @Override
    public Placeable[] placeableArray() {
        return placeholderArray();
    }

}
