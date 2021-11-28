package org.playuniverse.minecraft.wildcard.core.util.placeholder;

public final class Placeholder implements Placeable {

    private final String original;

    private final String key;
    private String value = "";

    public Placeholder(final String original, final String key) {
        this.original = original;
        this.key = key;
    }

    public Placeholder setValue(final String value) {
        this.value = value;
        return this;
    }

    protected String getOriginal() {
        return original;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String getPlaceKey() {
        return original;
    }

    @Override
    public String getPlaceValue() {
        return value;
    }

}
