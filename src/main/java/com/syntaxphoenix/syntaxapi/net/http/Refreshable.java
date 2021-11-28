package com.syntaxphoenix.syntaxapi.net.http;

@FunctionalInterface
public interface Refreshable<E> {

    public E refresh();

}
