package org.playuniverse.minecraft.wildcard.core.message.hover;

import org.playuniverse.minecraft.wildcard.core.message.HoverAction;
import org.playuniverse.minecraft.wildcard.core.message.PlatformComponent;

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
