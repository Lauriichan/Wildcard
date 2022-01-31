package org.playuniverse.minecraft.wildcard.core.message;

public final class PlatformClickEvent {
    
    private final ClickAction action;
    private final String value;
    
    public PlatformClickEvent(ClickAction action, String value) {
        this.action = action;
        this.value = value;
    }
    
    public ClickAction getAction() {
        return action;
    }
    
    public String getValue() {
        return value;
    }

}
