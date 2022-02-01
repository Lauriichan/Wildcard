package me.lauriichan.minecraft.wildcard.core.util.registry;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class UniqueRegistry<V extends IUnique> implements IRegistry<String, V> {

    private final ConcurrentHashMap<String, V> map = new ConcurrentHashMap<>();

    @Override
    public V get(final String key) {
        final V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.values().stream().filter(unique -> unique.getName().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    @Override
    public V getOrElse(final String key, final V fallback) {
        final V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.values().stream().filter(unique -> unique.getName().equalsIgnoreCase(key)).findFirst().orElse(fallback);
    }

    public boolean register(final V value) {
        return register(value.getId(), value);
    }

    @Override
    public boolean register(final String key, final V value) {
        if (!key.equals(value.getId()) || map.containsKey(key)) {
            return false;
        }
        map.put(key, value);
        return true;
    }

    @Override
    public boolean unregister(final String key) {
        final V value = get(key);
        if (value == null) {
            return false;
        }
        map.remove(value.getId(), value);
        return true;
    }

    @Override
    public boolean isRegistered(final String key) {
        return map.containsKey(key) ? true : map.values().stream().anyMatch(value -> value.getName().equalsIgnoreCase(key));
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void dispose() {
        map.clear();
    }

}
