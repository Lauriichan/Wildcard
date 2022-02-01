package me.lauriichan.minecraft.wildcard.core.data.setting;

public final class Category {

    public static final Category ROOT = new Category("*");

    private final String name;

    public Category(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ISetting of(final String name, final Class<?> type) {
        return of(name, type, true);
    }

    public ISetting of(final String name, final Class<?> type, final boolean persistent) {
        return ISetting.of(name, this.name, type, persistent);
    }

    public void load(final Settings settings, final Class<?> type) {
        settings.loadComplex(name, type);
    }

    public void load(final Settings settings) {
        settings.loadPrimitives(name);
    }

    public ISetting[] get(final Settings settings) {
        return settings.getAll(name);
    }

    public void delete(final Settings settings, final String name) {
        settings.delete(this.name + '.' + name);
    }

}
