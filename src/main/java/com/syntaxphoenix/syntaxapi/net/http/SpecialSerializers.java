package com.syntaxphoenix.syntaxapi.net.http;

import com.syntaxphoenix.syntaxapi.json.JsonArray;
import com.syntaxphoenix.syntaxapi.json.JsonEntry;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;

public class SpecialSerializers {

    /*
     * Default serializers
     */

    /*
     * Json serializers
     */

    public static final JsonContentSerializer PLAIN = new JsonContentSerializer() {

        private final char[] CHARS = new char[] {
            ':',
            ' ',
            '\t',
            '\n',
            '-'
        };
        private final String NULL = "null";
        private final String NEXT = "==>";

        public String process(JsonValue<?> element) {
            StringBuilder builder = new StringBuilder();
            if (element == null || element.getType() == ValueType.NULL) {
                return NULL;
            }
            if (element.isPrimitive()) {
                return builder.append(element.getValue()).toString();
            }
            if (element.getType() == ValueType.ARRAY) {
                append(builder, (JsonArray) element, 0);
                return builder.toString();
            }
            if (element.getType() == ValueType.OBJECT) {
                append(builder, (JsonObject) element, 0);
                return builder.toString();
            }
            return NULL;
        }

        private boolean append(StringBuilder builder, JsonObject object, int depth) {

            for (JsonEntry<?> entry : object) {

                JsonValue<?> element = entry.getValue();

                for (int current = 0; current < depth; current++) {
                    builder.append(CHARS[2]);
                }

                builder.append(entry.getKey());
                builder.append(CHARS[0]);
                builder.append(CHARS[1]);

                if (element == null || element.getType() == ValueType.NULL) {
                    builder.append(NULL);
                    continue;
                }

                if (element.isPrimitive()) {
                    builder.append(element.getValue());
                    continue;
                }

                builder.append(CHARS[3]);

                if (element.getType() == ValueType.ARRAY) {
                    append(builder, (JsonArray) element, depth + 1);
                    continue;
                }

                if (element.getType() == ValueType.OBJECT) {
                    append(builder, (JsonObject) element, depth + 1);
                    continue;
                }

            }

            return true;
        }

        private boolean append(StringBuilder builder, JsonArray array, int depth) {

            for (JsonValue<?> element : array) {

                for (int current = 0; current < depth; current++) {
                    builder.append(CHARS[2]);
                }

                builder.append(CHARS[4]);
                builder.append(CHARS[0]);

                if (element == null || element.getType() == ValueType.NULL) {
                    builder.append(NULL);
                    continue;
                }

                if (element.isPrimitive()) {
                    builder.append(element.getValue());
                    continue;
                }

                builder.append(NEXT);
                builder.append(CHARS[3]);

                if (element.getType() == ValueType.ARRAY) {
                    append(builder, (JsonArray) element, depth + 1);
                    continue;
                }

                if (element.getType() == ValueType.OBJECT) {
                    append(builder, (JsonObject) element, depth + 1);
                    continue;
                }

            }

            return true;
        }

    };

}
