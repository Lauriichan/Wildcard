package me.lauriichan.minecraft.wildcard.core.data.setting.converter;

import com.syntaxphoenix.syntaxapi.json.JsonArray;
import com.syntaxphoenix.syntaxapi.json.JsonEntry;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

import me.lauriichan.minecraft.wildcard.core.data.setting.json.JsonConverter;
import me.lauriichan.minecraft.wildcard.core.settings.TranslationMap;

@SuppressWarnings("rawtypes")
public class TranslationConverter extends JsonConverter<JsonObject, TranslationMap> {

    public TranslationConverter() {
        super(JsonObject.class, TranslationMap.class);
    }

    @Override
    protected JsonObject asJson(final TranslationMap object) {
        final JsonObject json = new JsonObject();
        if (object.isEmpty()) {
            return json;
        }
        for (final Object rawKey : object.keySet()) {
            if (!(rawKey instanceof String)) {
                continue;
            }
            final String key = (String) rawKey;
            final Object value = object.get(key);
            if (value == null || !Primitives.isInstance(value)) {
                continue;
            }
            if (value instanceof String) {
                final String string = (String) value;
                if (!string.contains("\n")) {
                    json.set(key, string);
                    continue;
                }
                final String[] parts = string.split("\n");
                final JsonArray array = new JsonArray();
                for (int index = 0; index < parts.length; index++) {
                    array.add(parts[index]);
                }
                json.set(key, array);
                continue;
            }
            json.set(key, value);
        }
        return json;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TranslationMap fromJson(final JsonObject json) {
        final TranslationMap map = new TranslationMap();
        for (final JsonEntry<?> entry : json) {
            if (entry.getType() == ValueType.NULL) {
                continue;
            }
            if (entry.getType().isPrimitive()) {
                map.put(entry.getKey(), entry.getValue().getValue().toString());
                continue;
            }
            if (entry.getType() != ValueType.ARRAY) {
                continue;
            }
            final JsonArray array = (JsonArray) entry.getValue();
            final StringBuilder builder = new StringBuilder();
            for (final JsonValue<?> value : array) {
                if (!value.hasType(ValueType.STRING)) {
                    continue;
                }
                builder.append((String) value.getValue()).append('\n');
            }
            map.put(entry.getKey(), builder.substring(0, builder.length() - 1));
        }
        return map;
    }

}
