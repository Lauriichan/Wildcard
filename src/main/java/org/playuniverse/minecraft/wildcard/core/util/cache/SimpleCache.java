package org.playuniverse.minecraft.wildcard.core.util.cache;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.function.Consumer;

public final class SimpleCache<K, V> implements Cache<K, V> {

    private final HashMap<K, CacheEntry<V>> entries = new HashMap<>();

    private final K[] emptyKeys;
    private final int cacheTime;

    private Consumer<V> removeAction;

    @SuppressWarnings("unchecked")
    public SimpleCache(final Class<K> clazz, final int cacheTime) {
        this.emptyKeys = (K[]) Array.newInstance(clazz, 0);
        this.cacheTime = cacheTime;
    }

    @Override
    public void setRemoveAction(final Consumer<V> removeAction) {
        this.removeAction = removeAction;
    }

    @Override
    public Consumer<V> getRemoveAction() {
        return removeAction;
    }

    @Override
    public void put(final K key, final V value) {
        if (key == null || value == null) {
            return; // No null values
        }
        entries.put(key, new CacheEntry<>(value));
    }

    @Override
    public boolean has(final K key) {
        return key != null && entries.containsKey(key);
    }

    @Override
    public V remove(final K key) {
        final CacheEntry<V> entry = entries.remove(key);
        return entry == null ? null : entry.getValue();
    }

    @Override
    public V get(final K key) {
        final CacheEntry<V> entry = entries.get(key);
        return entry == null ? null : entry.getValue();
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public void tick() {
        final K[] keys = entries.keySet().toArray(emptyKeys);
        for (final K key : keys) {
            final CacheEntry<V> entry = entries.get(key);
            if (entry == null) {
                continue;
            }
            entry.update();
            if (entry.getTime() >= cacheTime) {
                entries.remove(key, entry);
            }
        }
    }

}
