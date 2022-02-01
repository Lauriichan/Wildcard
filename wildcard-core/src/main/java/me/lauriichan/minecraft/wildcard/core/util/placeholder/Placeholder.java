package me.lauriichan.minecraft.wildcard.core.util.placeholder;

public final class Placeholder implements Placeable {

    private final String original;

    private final String key;
    private String value = "";

    public Placeholder(final String original, final String key) {
        this.original = original == null ? "" : original;
        this.key = key == null ? "" : key;
    }

    public Placeholder setValue(final String value) {
        this.value = value == null ? "" : value;
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
