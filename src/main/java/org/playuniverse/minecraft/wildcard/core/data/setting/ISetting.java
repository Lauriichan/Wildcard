package org.playuniverse.minecraft.wildcard.core.data.setting;

public interface ISetting {

    String getName();

    String getCategory();

    boolean isPersistent();

    ISetting setPersistent(boolean persistent);

    boolean isValid();

    Object get();

    Class<?> getType();

    boolean set(Object value);

    default <E> E getAs(final Class<E> clazz) {
        final Object object = get();
        return hasType(clazz) ? clazz.cast(object) : null;
    }

    default boolean hasType(final Class<?> clazz) {
        final Object object = get();
        return object != null && clazz.isAssignableFrom(object.getClass());
    }

    default String asCompact() {
        return getCategory() + '.' + getName();
    }

    static ISetting of(final String name, final String category, final Class<?> type) {
        return of(name, category, type, true);
    }

    static ISetting of(final String name, final String category, final Class<?> type, final boolean persistent) {
        if (type == null || name == null) {
            return NullSetting.NULL;
        }
        final ValueSetting setting = new ValueSetting(name, category == null ? Category.ROOT.getName() : category, type);
        setting.setPersistent(persistent);
        return setting;
    }

}
