package com.syntaxphoenix.syntaxapi.net.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.syntaxphoenix.syntaxapi.json.JsonArray;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;
import com.syntaxphoenix.syntaxapi.json.value.JsonNull;
import com.syntaxphoenix.syntaxapi.utils.java.Streams;
import com.syntaxphoenix.syntaxapi.utils.net.JsonHelper;

public class Response {

    private final JsonObject headers;
    private final int responseCode;
    private final byte[] response;

    public Response(int responseCode, byte[] response, Map<String, List<String>> headerMap) {
        this.responseCode = responseCode;
        this.response = response;
        this.headers = (JsonObject) JsonHelper.from(headerMap);
    }

    /*
     * Header management
     */

    public boolean has(String header) {
        return headers.has(header);
    }

    public boolean hasArray(String header) {
        return headers.has(header, ValueType.ARRAY);
    }

    public boolean hasObject(String header) {
        return headers.has(header, ValueType.OBJECT);
    }

    public JsonValue<?> get(String header) {
        return headers.get(header);
    }

    public JsonArray getArray(String header) {
        return (JsonArray) headers.get(header);
    }

    public JsonObject getObject(String header) {
        return (JsonObject) headers.get(header);
    }

    /*
     * Getters
     */

    public int getCode() {
        return responseCode;
    }

    public byte[] getResponseBytes() {
        return response;
    }

    public String getResponse() {
        try {
            return Streams.toString(new ByteArrayInputStream(response));
        } catch (IOException e) {
            return new String(response);
        }
    }

    public JsonValue<?> getResponseAsJson() {
        try {
            return new JsonParser().fromString(getResponse());
        } catch (IOException e) {
            return JsonNull.get();
        }
    }

    public JsonObject getResponseHeaders() {
        return headers;
    }

}
