package org.playuniverse.minecraft.wildcard.core.util.registry;

public class OrderedTypedRegistry<T extends ITyped<?>> extends OrderedRegistry<Class<?>, T> {

    public T getFor(final Object object) {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        final Class<?>[] array = map.keySet().toArray(Class<?>[]::new);
        for (final Class<?> key : array) {
            if (!key.equals(clazz)) {
                continue;
            }
            return get(key);
        }
        for (final Class<?> key : array) {
            if (!key.isAssignableFrom(clazz)) {
                continue;
            }
            return get(key);
        }
        return null;
    }

    @Override
    public T getOrElse(final Class<?> key, final T value) {
        final T output = get(key);
        if (output == null) {
            if (value.getType().equals(key)) {
                register(value.getType(), value);
                return value;
            }
            return null;
        }
        return output;
    }

    public boolean register(final T typed) {
        if (map.containsKey(typed.getType())) {
            return false;
        }
        map.put(typed.getType(), typed);
        return true;
    }

    @Override
    public boolean register(final Class<?> key, final T value) {
        if (!key.equals(value.getType()) || map.containsKey(key)) {
            return false;
        }
        map.put(key, value);
        return true;
    }

}
