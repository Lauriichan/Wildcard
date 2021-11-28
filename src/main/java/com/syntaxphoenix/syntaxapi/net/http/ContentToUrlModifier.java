package com.syntaxphoenix.syntaxapi.net.http;

import java.net.URL;

import com.syntaxphoenix.syntaxapi.utils.net.UrlReflection;

@FunctionalInterface
public interface ContentToUrlModifier {

    public static final ContentToUrlModifier URL_ENCODED = (url, content, serializer) -> {
        if (serializer != null && content != null) {
            UrlReflection.applyQuery(url, '?' + serializer.process(content));
        }
    };

    public void apply(URL url, RequestData<?> content, ContentSerializer serializer);

}
