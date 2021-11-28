package com.syntaxphoenix.syntaxapi.net.http;

@FunctionalInterface
public interface NamedType {

    public String type();

    public default boolean has(String extension) {
        return false;
    }

}
