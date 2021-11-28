package com.syntaxphoenix.syntaxapi.net.http;

import com.syntaxphoenix.syntaxapi.json.JsonValue;

@FunctionalInterface
public interface JsonContentSerializer extends ContentSerializer {

    @Override
    default String process(RequestData<?> parameters) {
        Object value = parameters.getValue();
        return value instanceof JsonValue ? process((JsonValue<?>) value) : null;
    }

    public String process(JsonValue<?> object);

}
