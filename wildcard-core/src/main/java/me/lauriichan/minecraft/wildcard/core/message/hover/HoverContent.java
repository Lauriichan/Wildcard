package me.lauriichan.minecraft.wildcard.core.message.hover;

import me.lauriichan.minecraft.wildcard.core.message.HoverAction;

public abstract class HoverContent {

    private final HoverAction action;

    public HoverContent(HoverAction action) {
        this.action = action;
    }

    public final HoverAction getAction() {
        return action;
    }

}
