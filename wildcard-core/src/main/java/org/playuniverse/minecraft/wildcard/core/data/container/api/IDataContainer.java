package org.playuniverse.minecraft.wildcard.core.data.container.api;

import java.util.Set;

import com.syntaxphoenix.syntaxapi.utils.key.IKey;

public interface IDataContainer {

    IDataAdapterRegistry<?> getRegistry();

    IDataAdapterContext getContext();

    boolean has(String key);

    boolean has(IKey key);

    boolean has(String key, IDataType<?, ?> type);

    boolean has(IKey key, IDataType<?, ?> type);

    Object get(String key);

    Object get(IKey key);

    <E> E get(String key, IDataType<?, E> type);

    <E> E get(IKey key, IDataType<?, E> type);

    boolean remove(String key);

    boolean remove(IKey key);

    <V, E> void set(String key, E value, IDataType<V, E> type);

    <V, E> void set(IKey key, E value, IDataType<V, E> type);

    Set<String> getKeyspaces();

    IKey[] getKeys();

    boolean isEmpty();

    int size();

}
