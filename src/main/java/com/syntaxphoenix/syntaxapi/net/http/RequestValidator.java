package com.syntaxphoenix.syntaxapi.net.http;

@FunctionalInterface
public interface RequestValidator {

    public static final RequestValidator DEFAULT_VALIDATOR = (writer, request) -> RequestContent.NEEDED
        .message(!request.hasHeader("Content-Length"));

    public RequestContent parseContent(HttpWriter writer, ReceivedRequest request) throws Exception;

}
