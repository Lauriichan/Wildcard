package me.lauriichan.minecraft.wildcard.core.data.setting.converter;

import java.io.File;

import com.syntaxphoenix.syntaxapi.json.value.JsonString;

import me.lauriichan.minecraft.wildcard.core.data.setting.json.JsonConverter;

public final class FileConverter extends JsonConverter<JsonString, File> {

    public FileConverter() {
        super(JsonString.class, File.class);
    }

    @Override
    protected JsonString asJson(final File object) {
        return new JsonString(object.getPath());
    }

    @Override
    protected File fromJson(final JsonString json) {
        return new File(json.getValue());
    }

}
