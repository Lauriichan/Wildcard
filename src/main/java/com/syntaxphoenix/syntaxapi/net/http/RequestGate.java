package com.syntaxphoenix.syntaxapi.net.http;

@FunctionalInterface
public interface RequestGate {

    public RequestState acceptRequest(HttpWriter writer, ReceivedRequest request) throws Exception;

}
