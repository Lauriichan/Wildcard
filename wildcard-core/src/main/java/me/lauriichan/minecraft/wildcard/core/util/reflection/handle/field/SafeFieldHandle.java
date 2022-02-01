package me.lauriichan.minecraft.wildcard.core.util.reflection.handle.field;

import java.lang.reflect.Field;

public class SafeFieldHandle implements IFieldHandle<Field> {

    private final Field handle;

    public SafeFieldHandle(final Field handle) {
        this.handle = handle;
    }

    @Override
    public Object getValue() {
        try {
            return handle.get(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return null;
        }
    }

    @Override
    public Object getValue(final Object source) {
        try {
            return handle.get(source);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return null;
        }
    }

    @Override
    public IFieldHandle<Field> setValue(final Object value) {
        try {
            handle.set(null, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
        }
        return this;
    }

    @Override
    public IFieldHandle<Field> setValue(final Object source, final Object value) {
        try {
            handle.set(source, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
        }
        return this;
    }

    @Override
    public Field getHandle() {
        return handle;
    }

    @Override
    public boolean isUnsafe() {
        return false;
    }

}
