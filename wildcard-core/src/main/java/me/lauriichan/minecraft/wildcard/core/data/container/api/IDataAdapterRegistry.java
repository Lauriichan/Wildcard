package me.lauriichan.minecraft.wildcard.core.data.container.api;

public interface IDataAdapterRegistry<B> {

    Class<B> getBase();

    boolean has(Class<?> clazz); // == isRegistered

    Object extract(B base);

    B wrap(Object value);

}
