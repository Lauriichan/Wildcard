package me.lauriichan.minecraft.wildcard.core.data.container;

import com.syntaxphoenix.syntaxapi.utils.key.IKey;
import com.syntaxphoenix.syntaxapi.utils.key.NamespacedKey;

import me.lauriichan.minecraft.wildcard.core.data.container.api.IDataAdapterRegistry;
import me.lauriichan.minecraft.wildcard.core.data.container.api.IDataContainer;
import me.lauriichan.minecraft.wildcard.core.data.container.api.IDataType;
import me.lauriichan.minecraft.wildcard.core.data.container.util.NumberConversion;

public abstract class AbstractDataContainer<B> implements IDataContainer {

    protected final IDataAdapterRegistry<B> registry;

    public AbstractDataContainer(final IDataAdapterRegistry<B> registry) {
        this.registry = registry;
    }

    @Override
    public IDataAdapterRegistry<B> getRegistry() {
        return registry;
    }

    @Override
    public Object get(final String key) {
        final B raw = getRaw(key);
        if (raw == null) {
            return raw;
        }
        return registry.extract(raw);
    }

    @Override
    public <E> E get(final String key, final IDataType<?, E> type) {
        final Object value = registry.getBase().isAssignableFrom(type.getPrimitive()) ? getRaw(key) : get(key);
        if (value == null || !type.isPrimitive(value)) {
            if (Number.class.isAssignableFrom(type.getComplex())) {
                return NumberConversion.convert(0, type.getComplex());
            }
            return null;
        }
        final E output = type.fromPrimitiveObj(getContext(), value);
        if (output == null && Number.class.isAssignableFrom(type.getComplex())) {
            return NumberConversion.convert(0, type.getComplex());
        }
        return output;
    }

    @Override
    public boolean has(final String key, final IDataType<?, ?> type) {
        if (!has(key)) {
            return false;
        }
        final Object value = registry.getBase().isAssignableFrom(type.getPrimitive()) ? getRaw(key) : get(key);
        return value != null && type.isPrimitive(value);
    }

    @Override
    public <V, E> void set(final String key, final E value, final IDataType<V, E> type) {
        set(key, registry.wrap(type.toPrimitive(getContext(), value)));
    }

    /*
     * Key conversion
     */

    @Override
    public Object get(final IKey key) {
        return get(key.asString());
    }

    @Override
    public <E> E get(final IKey key, final IDataType<?, E> type) {
        return get(key.asString(), type);
    }

    @Override
    public <V, E> void set(final IKey key, final E value, final IDataType<V, E> type) {
        set(key.asString(), value, type);
    }

    @Override
    public boolean has(final IKey key) {
        return has(key.asString());
    }

    @Override
    public boolean has(final IKey key, final IDataType<?, ?> type) {
        return has(key.asString(), type);
    }

    @Override
    public boolean remove(final IKey key) {
        return remove(key.asString());
    }

    @Override
    public IKey[] getKeys() {
        return getKeyspaces().stream().map(NamespacedKey::fromString).toArray(IKey[]::new);
    }

    /*
     * Abstract
     */

    public abstract B getRaw(String key);

    public B getRaw(final IKey key) {
        return getRaw(key.asString());
    }

    public abstract void set(String key, B value);

    public void set(final IKey key, final B value) {
        set(key.asString(), value);
    }

}
