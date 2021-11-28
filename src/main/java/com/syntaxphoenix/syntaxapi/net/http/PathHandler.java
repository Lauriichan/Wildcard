package com.syntaxphoenix.syntaxapi.net.http;

@FunctionalInterface
public interface PathHandler {

    public void handlePath(HttpSender sender, HttpWriter writer, ReceivedRequest data) throws Exception;

}
