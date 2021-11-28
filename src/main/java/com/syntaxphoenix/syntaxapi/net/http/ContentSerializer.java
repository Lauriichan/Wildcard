package com.syntaxphoenix.syntaxapi.net.http;

@FunctionalInterface
public interface ContentSerializer {

    /*
     * 
     */

    public String process(RequestData<?> parameters);

}
