package com.syntaxphoenix.syntaxapi.net.http;

@FunctionalInterface
public interface RequestHandler {

    public boolean handleRequest(HttpSender sender, HttpWriter writer, ReceivedRequest request) throws Exception;

}
