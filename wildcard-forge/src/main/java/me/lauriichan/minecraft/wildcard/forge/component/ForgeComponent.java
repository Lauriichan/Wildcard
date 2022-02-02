package me.lauriichan.minecraft.wildcard.forge.component;

import java.awt.Color;
import java.util.Objects;

import me.lauriichan.minecraft.wildcard.core.message.Format;
import me.lauriichan.minecraft.wildcard.core.message.PlatformClickEvent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformHoverEvent;
import me.lauriichan.minecraft.wildcard.core.message.hover.HoverText;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public final class ForgeComponent extends PlatformComponent {

    private final ForgeComponentAdapter adapter;

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

    public ForgeComponent(final ForgeComponentAdapter adapter) {
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
    public ITextComponent getHandle() {
        StringTextComponent component = new StringTextComponent(text);
        Style style = Style.EMPTY;
        if (color != null) {
            style = style.withColor(net.minecraft.util.text.Color.fromRgb(color.getRGB() & 0xFFFFFF));
        }
        if (clickEvent != null) {
            switch (clickEvent.getAction()) {
            case COPY_TO_CLIPBOARD:
                style = style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, clickEvent.getValue()));
                break;
            case RUN_COMMAND:
                style = style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickEvent.getValue()));
                break;
            }
            System.out.println("Click");
        }
        if (hoverEvent != null) {
            switch (hoverEvent.getAction()) {
            case SHOW_TEXT:
                style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    adapter.asHandle(((HoverText) hoverEvent.getContent()).getComponents())[0]));
                break;
            }
            System.out.println("Hover");
        }
        if (formats[0]) {
            style = style.setObfuscated(true);
        }
        if (formats[1]) {
            style = style.withBold(true);
        }
        if (formats[2]) {
            style = style.setStrikethrough(true);
        }
        if (formats[3]) {
            style = style.setUnderlined(true);
        }
        if (formats[4]) {
            style = style.withItalic(true);
        }
        component.setStyle(style);
        return component;
    }

}
