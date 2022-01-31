package org.playuniverse.minecraft.wildcard.core.message;

import java.awt.Color;

public abstract class PlatformComponent {

    public void setColor(Format format) {
        setColor(format.getColor());
    }

    public abstract void setColor(Color color);

    public abstract Color getColor();

    public abstract void setFormat(Format format, boolean state);

    public abstract boolean getFormat(Format format);

    public final void loadFormat(PlatformComponent component) {
        for (Format format : Format.formatValues()) {
            setFormat(format, component.getFormat(format));
        }
        setColor(component.getColor());
    }

    public abstract void setText(String text);
    
    public abstract String getText();
    
    public abstract void setClickEvent(PlatformClickEvent event);
    
    public abstract PlatformClickEvent getClickEvent();
    
    public abstract void setHoverEvent(PlatformHoverEvent event);

    public abstract PlatformHoverEvent getHoverEvent();
    
    public abstract Object getHandle();
    
}
