package me.lauriichan.minecraft.wildcard.sponge.component;

import java.awt.Color;
import java.util.Objects;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import me.lauriichan.minecraft.wildcard.core.message.Format;
import me.lauriichan.minecraft.wildcard.core.message.PlatformClickEvent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformHoverEvent;
import me.lauriichan.minecraft.wildcard.core.message.hover.HoverText;

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
    public Text getHandle() {
        Text.Builder builder = Text.builder();
        if (color != null) {
            builder.color(SpongeColorAdapter.ADAPTER.getNearestColor(color));
        }
        if (clickEvent != null) {
            switch (clickEvent.getAction()) {
            case COPY_TO_CLIPBOARD:
                builder.onClick(TextActions.suggestCommand(clickEvent.getValue()));
                break;
            case RUN_COMMAND:
                builder.onClick(TextActions.runCommand(clickEvent.getValue()));
                break;
            }
        }
        if (hoverEvent != null) {
            switch (hoverEvent.getAction()) {
            case SHOW_TEXT:
                builder.onHover(TextActions.showText(adapter.asHandle(((HoverText) hoverEvent.getContent()).getComponents())[0]));
                break;
            }
        }
        if (formats[0]) {
            builder.style(TextStyles.OBFUSCATED);
        }
        if (formats[1]) {
            builder.style(TextStyles.BOLD);
        }
        if (formats[2]) {
            builder.style(TextStyles.STRIKETHROUGH);
        }
        if (formats[3]) {
            builder.style(TextStyles.UNDERLINE);
        }
        if (formats[4]) {
            builder.style(TextStyles.ITALIC);
        }
        return builder.build();
    }

}
