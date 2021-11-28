package com.syntaxphoenix.syntaxapi.net.http;

public enum StandardContentType implements ContentType {

    URL_ENCODED("application/x-www-form-urlencoded", DefaultSerializers.URL_ENCODED, DefaultDeserializers.URL_ENCODED,
        ContentToUrlModifier.URL_ENCODED),
    PLAIN_SPECIAL("text/plain", SpecialSerializers.PLAIN, null),
    JSON("application/json", DefaultSerializers.JSON, DefaultDeserializers.JSON);

    private final String type;

    private final ContentSerializer serializer;
    private final ContentDeserializer deserializer;

    private final ContentToUrlModifier urlModifier;

    private StandardContentType(String type, ContentSerializer serializer, ContentDeserializer deserializer,
        ContentToUrlModifier urlModifier) {
        this.type = type;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.urlModifier = urlModifier;
    }

    private StandardContentType(String type, ContentSerializer serializer, ContentDeserializer deserializer) {
        this.type = type;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.urlModifier = null;
    }

    /*
     * Getter
     */

    @Override
    public String type() {
        return type;
    }

    @Override
    public ContentSerializer serializer() {
        return serializer;
    }

    @Override
    public ContentDeserializer deserializer() {
        return deserializer;
    }

    @Override
    public ContentToUrlModifier urlModifier() {
        return urlModifier;
    }

    /*
     * static Getter
     */

    public static StandardContentType fromString(String value) {
        for (StandardContentType type : values()) {
            if (type.type().equals(value)) {
                return type;
            }
        }
        return null;
    }

}
