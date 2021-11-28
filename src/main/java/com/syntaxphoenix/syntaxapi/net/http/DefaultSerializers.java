package com.syntaxphoenix.syntaxapi.net.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.syntaxphoenix.syntaxapi.json.JsonEntry;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.io.JsonWriter;

public class DefaultSerializers {

    /*
     * Default serializers
     */

    public static final ContentSerializer PLAIN = null;

    /*
     * Json serializers
     */

    public static final JsonContentSerializer JSON = parameters -> {
        try {
            return new JsonWriter().toString(parameters);
        } catch (IOException e) {
            // Ignore because it's just a string
            return "";
        }
    };

    public static final JsonContentSerializer URL_ENCODED = parameters -> {
        if (parameters.getType() != ValueType.OBJECT) {
            return "";
        }
        JsonObject object = (JsonObject) parameters;
        if (object.size() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        try {
            for (JsonEntry<?> parameter : object) {
                builder.append(URLEncoder.encode(parameter.getKey(), "UTF-8"));
                builder.append('=');
                builder.append(URLEncoder.encode(parameter.getValue().toString(), "UTF-8"));
                builder.append('&');
            }
        } catch (UnsupportedEncodingException ignore) {
        }

        String value = builder.toString();
        return value.substring(0, value.length() - 1);
    };

}
