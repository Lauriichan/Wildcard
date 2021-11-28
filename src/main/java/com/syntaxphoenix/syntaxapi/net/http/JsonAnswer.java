package com.syntaxphoenix.syntaxapi.net.http;

import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;

public class JsonAnswer extends Answer<JsonObject> {

    private JsonObject object = new JsonObject();

    /*
     * 
     */

    public JsonAnswer(ContentType type) {
        super(type);
    }

    /*
     * 
     */

    public JsonValue<?> respond(String key) {
        return object.get(key);
    }

    /*
     * 
     */

    public JsonAnswer code(int code) {
        super.code(code);
        return this;
    }

    public JsonAnswer header(String key, Object value) {
        super.header(key, value);
        return this;
    }

    public JsonAnswer header(String key, String value) {
        super.header(key, value);
        return this;
    }

    public JsonAnswer respond(String key, String value) {
        if (value != null) {
            object.set(key, value);
        } else {
            object.remove(key);
        }
        return this;
    }

    public JsonAnswer respond(String key, JsonValue<?> element) {
        if (element != null) {
            object.set(key, element);
        } else {
            object.remove(key);
        }
        return this;
    }

    public JsonAnswer respond(JsonObject object) {
        this.object = object;
        return this;
    }

    /*
     * 
     */

    public JsonAnswer clearHeaders() {
        clearHeaders();
        return this;
    }

    public JsonAnswer clearResponse() {
        object = new JsonObject();
        return this;
    }

    /*
     * 
     */

    public boolean hasResponse() {
        return !object.isEmpty();
    }

    @Override
    public JsonObject getResponse() {
        return object;
    }

    @Override
    public byte[] serializeResponse() {
        return serializeString(object.toString());
    }

}
