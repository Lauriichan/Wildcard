package org.playuniverse.minecraft.wildcard.bungee.component;

import java.awt.Color;
import java.util.Objects;

import org.playuniverse.minecraft.wildcard.core.message.Format;
import org.playuniverse.minecraft.wildcard.core.message.PlatformClickEvent;
import org.playuniverse.minecraft.wildcard.core.message.PlatformComponent;
import org.playuniverse.minecraft.wildcard.core.message.PlatformHoverEvent;
import org.playuniverse.minecraft.wildcard.core.message.hover.HoverContent;
import org.playuniverse.minecraft.wildcard.core.message.hover.HoverText;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;

public final class BungeeComponent extends PlatformComponent {

    private final TextComponent component = new TextComponent();
    private final BungeeComponentAdapter adapter;

    private PlatformClickEvent clickEvent;
    private PlatformHoverEvent hoverEvent;
    private Color color;

    public BungeeComponent(BungeeComponentAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        adapter.getBungeeAdapter().applyColor(component, color);
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setFormat(Format format, boolean state) {
        switch (format) {
        case MAGIC:
            component.setObfuscated(state);
            return;
        case BOLD:
            component.setBold(state);
            return;
        case STRIKETHROUGH:
            component.setStrikethrough(state);
            return;
        case UNDERLINE:
            component.setUnderlined(state);
            return;
        case ITALIC:
            component.setItalic(state);
            return;
        default:
            return;
        }
    }

    @Override
    public boolean getFormat(Format format) {
        switch (format) {
        case MAGIC:
            return component.isObfuscated();
        case BOLD:
            return component.isBold();
        case STRIKETHROUGH:
            return component.isStrikethrough();
        case UNDERLINE:
            return component.isUnderlined();
        case ITALIC:
            return component.isItalic();
        default:
            return false;
        }
    }

    @Override
    public void setText(String text) {
        component.setText(text);
    }

    @Override
    public String getText() {
        return component.getText();
    }

    @Override
    public void setClickEvent(PlatformClickEvent event) {
        this.clickEvent = event;
        component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(event.getAction().name()), event.getValue()));
    }

    @Override
    public PlatformClickEvent getClickEvent() {
        return clickEvent;
    }

    @Override
    public void setHoverEvent(PlatformHoverEvent event) {
        this.hoverEvent = event;
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(event.getAction().name()), convertContent(event.getContent())));
    }

    private Content convertContent(HoverContent content) {
        Objects.requireNonNull(content);
        if (content instanceof HoverText) {
            return new Text(adapter.asHandle(((HoverText) content).getComponents()));
        }
        throw new IllegalArgumentException("Unknown Content type '" + content.getClass().getSimpleName() + "'!");
    }

    @Override
    public PlatformHoverEvent getHoverEvent() {
        return hoverEvent;
    }

    @Override
    public TextComponent getHandle() {
        return component;
    }

}
