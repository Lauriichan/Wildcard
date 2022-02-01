package me.lauriichan.minecraft.wildcard.core.util.placeholder;

public class ForkTemplate extends Template {

    private final Template parent;

    public ForkTemplate(final Template parent, final String key) {
        super(parent.getOriginal(), key, parent.getContent());
        this.parent = parent;
    }

    public Template getParent() {
        return parent;
    }

}
