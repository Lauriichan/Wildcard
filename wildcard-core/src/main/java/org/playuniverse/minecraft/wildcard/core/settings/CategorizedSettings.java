package org.playuniverse.minecraft.wildcard.core.settings;

import org.playuniverse.minecraft.wildcard.core.data.setting.Category;
import org.playuniverse.minecraft.wildcard.core.data.setting.ISetting;
import org.playuniverse.minecraft.wildcard.core.data.setting.Settings;
import org.playuniverse.minecraft.wildcard.core.util.Singleton;

public abstract class CategorizedSettings {

    private static final byte BYTE_ZERO = (byte) 0;
    private static final short SHORT_ZERO = (short) 0;

    protected final Category category;
    protected final Settings settings = Singleton.get(Settings.class);

    CategorizedSettings(final Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public Settings getSettings() {
        return settings;
    }

    public final void load() {
        category.load(settings);
    }

    public final <E> E getOrDefault(final String name, final Class<E> type, final E fallback) {
        final E value = get(name, type);
        if (value == null && fallback != null) {
            set(name, type, fallback);
            return fallback;
        }
        return value;
    }

    public final <E> E get(final String name, final Class<E> type) {
        final ISetting setting = settings.get(name, category);
        return setting.isValid() ? setting.getAs(type) : null;
    }

    public final <E> void set(final String name, final Class<E> type, final E value) {
        settings.put(category.of(name, type, true)).set(value);
    }

    public void setString(final String name, final String value) {
        set(name, String.class, value);
    }

    public String getString(final String name) {
        return getString(name, null);
    }

    public String getString(final String name, final String fallback) {
        return getOrDefault(name, String.class, fallback);
    }

    public void setInteger(final String name, final int value) {
        set(name, Number.class, value);
    }

    public int getInteger(final String name) {
        return getInteger(name, 0);
    }

    public int getInteger(final String name, final int fallback) {
        return getOrDefault(name, Number.class, fallback).intValue();
    }

    public void setShort(final String name, final short value) {
        set(name, Number.class, value);
    }

    public int getShort(final String name) {
        return getShort(name, SHORT_ZERO);
    }

    public int getShort(final String name, final short fallback) {
        return getOrDefault(name, Number.class, fallback).shortValue();
    }

    public void setByte(final String name, final byte value) {
        set(name, Number.class, value);
    }

    public int getByte(final String name) {
        return getByte(name, BYTE_ZERO);
    }

    public int getByte(final String name, final byte fallback) {
        return getOrDefault(name, Number.class, fallback).shortValue();
    }

    public void setBoolean(final String name, final boolean value) {
        set(name, Boolean.class, value);
    }

    public boolean getBoolean(final String name) {
        return getBoolean(name, false);
    }

    public boolean getBoolean(final String name, final boolean fallback) {
        return getOrDefault(name, Boolean.class, fallback);
    }

}
