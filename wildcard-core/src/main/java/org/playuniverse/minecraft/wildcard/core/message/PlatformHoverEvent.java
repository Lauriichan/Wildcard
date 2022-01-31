package org.playuniverse.minecraft.wildcard.core.message;

import org.playuniverse.minecraft.wildcard.core.message.hover.HoverContent;

public final class PlatformHoverEvent {

    private final HoverAction action;
    private final HoverContent content;

    public PlatformHoverEvent(HoverAction action, HoverContent content) {
        this.action = action;
        this.content = content;
    }

    public HoverAction getAction() {
        return action;
    }

    public HoverContent getContent() {
        return content;
    }

}
