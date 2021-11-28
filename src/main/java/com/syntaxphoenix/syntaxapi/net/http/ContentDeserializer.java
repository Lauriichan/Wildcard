package com.syntaxphoenix.syntaxapi.net.http;

@FunctionalInterface
public interface ContentDeserializer {

    /*
     * 
     */

    public RequestData<?> process(String value);

}
