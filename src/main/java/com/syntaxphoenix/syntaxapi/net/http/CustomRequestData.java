package com.syntaxphoenix.syntaxapi.net.http;

public class CustomRequestData<E> extends RequestData<E> {

    private final Class<E> type;
    private final E value;

    public CustomRequestData(Class<E> type, E value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public Class<E> getType() {
        return type;
    }

    @Override
    public E getValue() {
        return value;
    }

}
