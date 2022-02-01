package me.lauriichan.minecraft.wildcard.core.util.cache;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public final class ThreadSafeCache<K, V> implements Cache<K, V> {

    private final HashMap<K, CacheEntry<V>> entries = new HashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock write = lock.writeLock();
    private final Lock read = lock.readLock();

    private final K[] emptyKeys;
    private final int cacheTime;

    private Consumer<V> removeAction;

    @SuppressWarnings("unchecked")
    public ThreadSafeCache(final Class<K> clazz, final int cacheTime) {
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
        write.lock();
        try {
            entries.put(key, new CacheEntry<>(value));
        } finally {
            write.unlock();
        }
    }

    @Override
    public boolean has(final K key) {
        if (key == null) {
            return false;
        }
        read.lock();
        try {
            return entries.containsKey(key);
        } finally {
            read.unlock();
        }
    }

    @Override
    public V remove(final K key) {
        CacheEntry<V> entry;
        write.lock();
        try {
            entry = entries.remove(key);
        } finally {
            write.unlock();
        }
        return entry == null ? null : entry.getValue();
    }

    @Override
    public V get(final K key) {
        CacheEntry<V> entry;
        read.lock();
        try {
            entry = entries.get(key);
        } finally {
            read.unlock();
        }
        return entry == null ? null : entry.getValue();
    }

    @Override
    public void clear() {
        write.lock();
        try {
            entries.clear();
        } finally {
            write.unlock();
        }
    }

    @Override
    public void tick() {
        K[] keys;
        read.lock();
        try {
            keys = entries.keySet().toArray(emptyKeys);
        } finally {
            read.unlock();
        }
        for (final K key : keys) {
            CacheEntry<V> entry;
            read.lock();
            try {
                entry = entries.get(key);
            } finally {
                read.unlock();
            }
            if (entry == null) {
                continue;
            }
            entry.update();
            if (entry.getTime() >= cacheTime) {
                write.lock();
                try {
                    entries.remove(key, entry);
                } finally {
                    write.unlock();
                }
                if (removeAction != null) {
                    CompletableFuture.runAsync(() -> removeAction.accept(entry.getValue()));
                }
            }
        }
    }

}
