package com.syntaxphoenix.syntaxapi.net.http;

@FunctionalInterface
public interface RequestSerializer {

    public RequestData<?> serialize(byte[] data) throws Exception;

}
