package me.lauriichan.minecraft.wildcard.sponge.component;

import java.awt.Color;
import java.util.Objects;

import me.lauriichan.minecraft.wildcard.core.message.Format;
import me.lauriichan.minecraft.wildcard.core.message.PlatformClickEvent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformHoverEvent;
import me.lauriichan.minecraft.wildcard.core.message.hover.HoverText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class SpongeComponent extends PlatformComponent {

    private final SpongeComponentAdapter adapter;

    private final boolean[] formats = new boolean[] {
        false,
        false,
        false,
        false,
        false
    };

    private String text = "";
    private Color color;

    private PlatformClickEvent clickEvent;
    private PlatformHoverEvent hoverEvent;

    public SpongeComponent(final SpongeComponentAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setFormat(Format format, boolean state) {
        switch (format) {
        case MAGIC:
            formats[0] = state;
            return;
        case BOLD:
            formats[1] = state;
            return;
        case STRIKETHROUGH:
            formats[2] = state;
            return;
        case UNDERLINE:
            formats[3] = state;
            return;
        case ITALIC:
            formats[4] = state;
            return;
        default:
            return;
        }
    }

    @Override
    public boolean getFormat(Format format) {
        switch (format) {
        case MAGIC:
            return formats[0];
        case BOLD:
            return formats[1];
        case STRIKETHROUGH:
            return formats[2];
        case UNDERLINE:
            return formats[3];
        case ITALIC:
            return formats[4];
        default:
            return false;
        }
    }

    @Override
    public void setText(String text) {
        this.text = Objects.requireNonNull(text);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setClickEvent(PlatformClickEvent event) {
        this.clickEvent = event;
    }

    @Override
    public PlatformClickEvent getClickEvent() {
        return clickEvent;
    }

    @Override
    public void setHoverEvent(PlatformHoverEvent event) {
        this.hoverEvent = event;
    }

    @Override
    public PlatformHoverEvent getHoverEvent() {
        return hoverEvent;
    }

    @Override
    public Component getHandle() {
        TextComponent component = Component.empty();
        if (color != null) {
            component.color(TextColor.color(color.getRGB()));
        }
        if (clickEvent != null) {
            switch (clickEvent.getAction()) {
            case COPY_TO_CLIPBOARD:
                component.clickEvent(ClickEvent.copyToClipboard(clickEvent.getValue()));
                break;
            case RUN_COMMAND:
                component.clickEvent(ClickEvent.runCommand(clickEvent.getValue()));
                break;
            }
        }
        if (hoverEvent != null) {
            switch (hoverEvent.getAction()) {
            case SHOW_TEXT:
                component.hoverEvent(HoverEvent.showText(adapter.asHandle(((HoverText) hoverEvent.getContent()).getComponents())[0]));
                break;
            }
        }
        if (formats[0]) {
            component.decoration(TextDecoration.OBFUSCATED);
        }
        if (formats[1]) {
            component.decoration(TextDecoration.BOLD);
        }
        if (formats[2]) {
            component.decoration(TextDecoration.STRIKETHROUGH);
        }
        if (formats[3]) {
            component.decoration(TextDecoration.UNDERLINED);
        }
        if (formats[4]) {
            component.decoration(TextDecoration.ITALIC);
        }
        return component;
    }

}
