package me.lauriichan.minecraft.wildcard.core.data.setting.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;
import com.syntaxphoenix.syntaxapi.json.io.JsonWriter;
import com.syntaxphoenix.syntaxapi.json.value.JsonNull;
import com.syntaxphoenix.syntaxapi.reflection.AbstractReflect;
import com.syntaxphoenix.syntaxapi.utils.io.TextDeserializer;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;
import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

import me.lauriichan.minecraft.wildcard.core.data.setting.Serialize;
import me.lauriichan.minecraft.wildcard.core.util.InstanceCreator;
import me.lauriichan.minecraft.wildcard.core.util.Singleton;

public final class JsonIO {

    private JsonIO() {}

    private static final List<JsonConverter<?, ?>> CONVERTERS = Collections.synchronizedList(new ArrayList<>());

    public static final TextDeserializer<JsonValue<?>> PARSER = new JsonSerializer(new JsonParser());
    public static final JsonWriter WRITER = new JsonWriter().setPretty(true).setSpaces(true).setIndent(2);

    public static void register(final JsonConverter<?, ?> converter) {
        if (CONVERTERS.contains(converter)) {
            return;
        }
        CONVERTERS.add(converter);
    }

    public static JsonConverter<?, ?> get(final Class<?> json, final Class<?> object) {
        if(json == null) {
            return null;
        }
        for (final JsonConverter<?, ?> converter : CONVERTERS) {
            if (!converter.getJsonType().isAssignableFrom(json) || !converter.getObjectType().isAssignableFrom(object)) {
                System.out.println(converter.getJsonType().isAssignableFrom(json) + " / " + json.isAssignableFrom(converter.getJsonType()));
                System.out.println(converter.getObjectType().isAssignableFrom(object) + " / " + object.isAssignableFrom(converter.getObjectType()));
                continue;
            }
            return converter;
        }
        return null;
    }

    public static JsonValue<?> fromObject(final Object object) {
        if (object == null) {
            return JsonNull.get();
        }
        final Class<?> clazz = object.getClass();
        if (Primitives.isInstance(object)) {
            return JsonValue.fromPrimitive(object);
        }
        final ArrayList<Field> fields = new ArrayList<>();
        putClass(fields, clazz);
        if (fields.isEmpty()) {
            return JsonNull.get();
        }
        if (fields.size() == 1) {
            return getValueAsJson(object, fields.get(0));
        }
        final JsonObject output = new JsonObject();
        for (final Field field : fields) {
            final JsonValue<?> value = getValueAsJson(object, field);
            if (value.hasType(ValueType.NULL)) {
                continue;
            }
            output.set(field.getName(), value);
        }
        return output;
    }

    public static Object toObject(final JsonValue<?> value, final Class<?> clazz) {
        if (Primitives.isInstance(clazz)) {
            return value.getValue();
        }
        final ArrayList<Field> fields = new ArrayList<>();
        putClass(fields, clazz);
        try {
            final Object object = InstanceCreator.create(clazz, Singleton.getInjects());
            if (fields.isEmpty()) {
                return object;
            }
            if (fields.size() == 1) {
                setValue(object, fields.get(0), value);
                return object;
            }
            final JsonObject jsonObject = (JsonObject) value;
            for (final Field field : fields) {
                setValue(object, field, jsonObject.get(field.getName()));
            }
            return object;
        } catch (final Exception e) {
            return null;
        }
    }

    /*
     * Field resolver
     */

    private static void putClass(final ArrayList<Field> fields, final Class<?> clazz) {
        putFields(fields, clazz.getFields());
        putFields(fields, clazz.getDeclaredFields());
        if (clazz.getSuperclass() == null) {
            return;
        }
        putClass(fields, clazz.getSuperclass());
    }

    private static void putFields(final ArrayList<Field> fields, final Field[] addition) {
        for (final Field field : addition) {
            if (Modifier.isStatic(field.getModifiers()) || (field.getAnnotation(Serialize.class) == null) || fields.contains(field)) {
                continue;
            }
            fields.add(field);
        }
    }

    /*
     * Reflections
     */

    private static JsonValue<?> getValueAsJson(final Object instance, final Field field) {
        final Object object = getValue(instance, field);
        if (object == null) {
            return JsonNull.get();
        }
        if (Primitives.isInstance(object)) {
            return JsonValue.fromPrimitive(object);
        }
        JsonConverter<?, ?> converter = get(null, object.getClass());
        if (converter == null) {
            converter = get(null, field.getType());
            if (converter == null) {
                return JsonNull.get();
            }
        }
        final JsonValue<?> output = converter.asAbstractJson(object);
        if (output == null) {
            return JsonNull.get();
        }
        return output;
    }

    @SuppressWarnings("deprecation")
    private static Object getValue(final Object instance, final Field field) {
        try {
            if (field.isAccessible()) {
                return field.get(instance);
            }
            field.setAccessible(true);
            final Object value = field.get(instance);
            field.setAccessible(false);
            return value;
        } catch (IllegalArgumentException | IllegalAccessException exp) {
            return null;
        }
    }

    private static void setValue(final Object instance, final Field field, final JsonValue<?> value) {
        if (value == null || value.hasType(ValueType.NULL)) {
            return;
        }
        final Class<?> type = field.getType();
        Object converted = type.isAssignableFrom(value.getClass()) ? value : value.getValue();
        if (!Primitives.isInstance(type) && !type.isAssignableFrom(converted.getClass())) {
            final JsonConverter<?, ?> converter = get(value.getClass(), type);
            if (converter == null) {
                return;
            }
            converted = converter.fromAbstractJson(value);
            if (converted == null) {
                return;
            }
        }
        setValue(instance, field, converted);
    }
    
    public static void setValue(final Object instance, final Field field, final Object value) {
        try {
            final int modifier = field.getModifiers();
            if (Modifier.isFinal(field.getModifiers())) {
                AbstractReflect.FIELD.setFieldValue(field, "modify", modifier & ~Modifier.FINAL);
                if (field.canAccess(instance)) {
                    field.set(instance, value);
                    AbstractReflect.FIELD.setFieldValue(field, "modify", modifier);
                    return;
                }
                field.setAccessible(true);
                field.set(instance, value);
                field.setAccessible(false);
                AbstractReflect.FIELD.setFieldValue(field, "modify", modifier);
                return;
            }
            if (field.canAccess(instance)) {
                field.set(instance, value);
                return;
            }
            field.setAccessible(true);
            field.set(instance, value);
            field.setAccessible(false);
        } catch (IllegalArgumentException | IllegalAccessException exp) {
            System.err.println(Exceptions.stackTraceToString(exp));
            return;
        }
    }

}
