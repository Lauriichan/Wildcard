package me.lauriichan.minecraft.wildcard.core.data.setting.json;

import com.syntaxphoenix.syntaxapi.json.JsonValue;

public abstract class JsonConverter<J extends JsonValue<?>, O> {

    private final Class<J> jsonType;
    private final Class<O> objectType;

    public JsonConverter(final Class<J> json, final Class<O> object) {
        this.jsonType = json;
        this.objectType = object;
        JsonIO.register(this);
    }

    public final Class<J> getJsonType() {
        return jsonType;
    }

    public final Class<O> getObjectType() {
        return objectType;
    }

    public final O fromAbstractJson(final JsonValue<?> value) {
        if (!getJsonType().isAssignableFrom(value.getClass())) {
            return null;
        }
        return fromJson(getJsonType().cast(value));
    }

    public final J asAbstractJson(final Object object) {
        if (!getObjectType().isAssignableFrom(object.getClass())) {
            return null;
        }
        return asJson(getObjectType().cast(object));
    }

    protected abstract J asJson(O object);

    protected abstract O fromJson(J json);

}
