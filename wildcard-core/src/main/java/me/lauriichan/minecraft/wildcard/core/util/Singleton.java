package me.lauriichan.minecraft.wildcard.core.util;

import java.lang.reflect.Modifier;
import java.util.HashMap;

public final class Singleton {

    public static final Singleton INSTANCE = new Singleton();

    public static <E> E get(final Class<E> clazz) {
        return INSTANCE.buildOrGet(clazz);
    }

    public static boolean inject(final Object object) {
        return INSTANCE.put(object);
    }

    public static Object[] getInjects() {
        return INSTANCE.getInjectsImpl();
    }

    private final HashMap<Class<?>, Object> instances = new HashMap<>();
    private final DynamicArray<Object> array = new DynamicArray<>();
    private final DynamicArray<Class<?>> types = new DynamicArray<>();

    private Singleton() {}

    private Object[] getInjectsImpl() {
        return array.asArray();
    }

    public boolean create(final Class<?> clazz) {
        try {
            final Object object = InstanceCreator.create(clazz, array.asArray());
            if (object == null) {
                return false;
            }
            instances.put(clazz, object);
            types.add(clazz);
            array.add(object);
            return true;
        } catch (final Exception exp) {
            return false;
        }
    }

    public boolean put(final Object object) {
        final int index = array.indexOf(object);
        if (index != -1) {
            return false;
        }
        array.add(object);
        return true;
    }

    public <E> E buildOrGet(final Class<E> clazz) {
        if (!Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface()) {
            if (instances.containsKey(clazz) || create(clazz)) {
                return clazz.cast(instances.get(clazz));
            }
            return null;
        }
        final Class<?>[] types = this.types.asArray(Class[]::new);
        for (final Class<?> type : types) {
            if (!clazz.isAssignableFrom(type)) {
                continue;
            }
            return clazz.cast(instances.get(type));
        }
        return null;
    }

}
