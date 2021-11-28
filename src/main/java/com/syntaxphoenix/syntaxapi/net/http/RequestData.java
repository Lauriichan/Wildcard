package com.syntaxphoenix.syntaxapi.net.http;

public abstract class RequestData<T> {

    public abstract Class<T> getType();

    public abstract T getValue();

}
