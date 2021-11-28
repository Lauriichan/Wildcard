package com.syntaxphoenix.syntaxapi.net.http;

import java.util.HashMap;

public class Cookie {

    public static Cookie of(String key, Object value) {
        return new Cookie(key, value);
    }

    private final HashMap<String, Object> properties = new HashMap<>();
    private final String name;
    private final Object value;

    private Cookie(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public Cookie add(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public Cookie add(String key) {
        return add(key, null);
    }

    public Cookie remove(String key) {
        properties.remove(key);
        return this;
    }

}
