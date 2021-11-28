package com.syntaxphoenix.syntaxapi.net.http;

public enum RequestType {

    GET,
    HEAD,
    PUT(true),
    POST(true),
    DELETE(true),
    OPTIONS(true),
    PATCH(true),
    CONNECT,
    TRACE,
    NONE;

    private final boolean output;

    private RequestType() {
        output = false;
    }

    private RequestType(boolean state) {
        output = state;
    }

    public boolean hasOutput() {
        return output;
    }

    public static RequestType fromString(String value) {
        value = value.toUpperCase();
        for (RequestType type : values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        return NONE;
    }

}
