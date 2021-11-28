package com.syntaxphoenix.syntaxapi.net.http;

import java.io.IOException;

import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;

public class DefaultDeserializers {

    /*
     * Default deserializers
     */

    public static final ContentSerializer PLAIN = null;

    /*
     * Json deserializers
     */

    public static final JsonContentDeserializer JSON = value -> {
        try {
            JsonValue<?> output = new JsonParser().fromString(value);
            if (output.getType() == ValueType.OBJECT) {
                return (JsonObject) output;
            }
        } catch (IOException e) {
            // Ignore
        }
        return new JsonObject();
    };

    public static final JsonContentDeserializer URL_ENCODED = value -> {
        JsonObject output = new JsonObject();
        String[] entries = (value = value.replaceFirst("\\?", "")).contains("&") ? value.split("&")
            : new String[] {
                value
        };
        for (int index = 0; index < entries.length; index++) {
            String current = entries[index];
            if (!current.contains("=")) {
                continue;
            }
            String[] entry = current.split("=", 2);
            output.set(entry[0], entry[1]);
        }
        return output;
    };

}
