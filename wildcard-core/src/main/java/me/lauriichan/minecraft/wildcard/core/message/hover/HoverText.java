package me.lauriichan.minecraft.wildcard.core.message.hover;

import me.lauriichan.minecraft.wildcard.core.message.HoverAction;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;

public final class HoverText extends HoverContent {

    private final PlatformComponent[] components;

    public HoverText(PlatformComponent... components) {
        super(HoverAction.SHOW_TEXT);
        this.components = components;
    }

    public PlatformComponent[] getComponents() {
        return components;
    }

}
