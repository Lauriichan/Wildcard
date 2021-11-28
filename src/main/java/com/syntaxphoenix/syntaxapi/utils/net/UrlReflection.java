package com.syntaxphoenix.syntaxapi.utils.net;

import java.net.URL;
import java.util.Objects;

import com.syntaxphoenix.syntaxapi.reflection.AbstractReflect;
import com.syntaxphoenix.syntaxapi.reflection.Reflect;

public final class UrlReflection {
    
    private UrlReflection() {}

    public static final AbstractReflect URL_REFLECT = new Reflect(URL.class).searchField("query", "query").searchField("file", "file");

    public static void applyQuery(URL url, String query) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(query);
        URL_REFLECT.setFieldValue(url, "file", url.getPath() + query);
        URL_REFLECT.setFieldValue(url, "query", query.startsWith("?") ? query.replaceFirst("\\?", "") : query);
    }

}
