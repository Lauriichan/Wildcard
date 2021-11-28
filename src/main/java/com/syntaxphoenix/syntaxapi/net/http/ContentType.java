package com.syntaxphoenix.syntaxapi.net.http;

import java.net.URL;

public interface ContentType extends NamedType {

    /*
     * Interfaces
     */

    public ContentSerializer serializer();

    public ContentDeserializer deserializer();

    public ContentToUrlModifier urlModifier();

    /*
     * Defaults
     */

    public default boolean supportsUrlModification() {
        return urlModifier() != null;
    }

    public default void modifyUrl(URL url, RequestData<?> object) {
        ContentToUrlModifier modifier = urlModifier();
        if (modifier != null) {
            modifier.apply(url, object, serializer());
        }
    }

    public default String serialize(RequestData<?> object) {
        ContentSerializer serializer = serializer();
        if (serializer == null) {
            return "";
        }
        return serializer.process(object);
    }

    public default RequestData<?> deserialize(String value) {
        ContentDeserializer deserializer = deserializer();
        if (deserializer == null) {
            return null;
        }
        return deserializer.process(value);
    }

    /*
     * Builder
     */

    public static ContentType build(String type) {
        return new ContentType() {
            @Override
            public String type() {
                return type;
            }

            @Override
            public ContentDeserializer deserializer() {
                return null;
            }

            @Override
            public ContentSerializer serializer() {
                return null;
            }

            @Override
            public ContentToUrlModifier urlModifier() {
                return null;
            }
        };
    }

    public static ContentType build(String type, ContentType content) {
        return new ContentType() {
            @Override
            public String type() {
                return type;
            }

            @Override
            public ContentDeserializer deserializer() {
                return content.deserializer();
            }

            @Override
            public ContentSerializer serializer() {
                return content.serializer();
            }

            @Override
            public ContentToUrlModifier urlModifier() {
                return content.urlModifier();
            }
        };
    }

    public static ContentType build(String type, ContentSerializer serializer, ContentDeserializer deserializer) {
        return new ContentType() {
            @Override
            public String type() {
                return type;
            }

            @Override
            public ContentDeserializer deserializer() {
                return deserializer;
            }

            @Override
            public ContentSerializer serializer() {
                return serializer;
            }

            @Override
            public ContentToUrlModifier urlModifier() {
                return null;
            }
        };
    }

    public static ContentType build(String type, ContentSerializer serializer, ContentDeserializer deserializer,
        ContentToUrlModifier modifier) {
        return new ContentType() {
            @Override
            public String type() {
                return type;
            }

            @Override
            public ContentDeserializer deserializer() {
                return deserializer;
            }

            @Override
            public ContentSerializer serializer() {
                return serializer;
            }

            @Override
            public ContentToUrlModifier urlModifier() {
                return modifier;
            }
        };
    }

}
