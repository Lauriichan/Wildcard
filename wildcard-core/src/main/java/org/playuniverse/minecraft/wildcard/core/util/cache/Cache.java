package org.playuniverse.minecraft.wildcard.core.util.cache;

import java.util.function.Consumer;

public interface Cache<K, V> {

    void put(K key, V value);

    boolean has(K key);

    V remove(K key);

    V get(K key);

    void clear();

    void tick();

    void setRemoveAction(Consumer<V> removeAction);

    Consumer<V> getRemoveAction();

}
