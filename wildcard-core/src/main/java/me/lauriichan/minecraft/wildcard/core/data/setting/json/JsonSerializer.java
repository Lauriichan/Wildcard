package me.lauriichan.minecraft.wildcard.core.data.setting.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.stream.Collectors;

import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;
import com.syntaxphoenix.syntaxapi.utils.io.TextDeserializer;

final class JsonSerializer implements TextDeserializer<JsonValue<?>> {

    private final JsonParser parser;

    public JsonSerializer(final JsonParser parser) {
        this.parser = parser;
    }

    @Override
    public JsonValue<?> fromString(final String str) throws IOException {
        return parser.fromReader(new StringReader(str));
    }

    @Override
    public JsonValue<?> fromReader(final Reader reader) throws IOException {
        return fromString(asString(reader));
    }

    private String asString(final Reader reader) {
        return asBuffered(reader).lines().collect(Collectors.joining(System.lineSeparator()));
    }

    private BufferedReader asBuffered(final Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

}
