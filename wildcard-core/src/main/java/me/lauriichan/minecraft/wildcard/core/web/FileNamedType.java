package me.lauriichan.minecraft.wildcard.core.web;

import java.util.Arrays;
import java.util.List;

import com.syntaxphoenix.syntaxapi.net.http.NamedType;

public enum FileNamedType implements NamedType {

    PLAIN("text/plain", "txt"),;

    private final String type;
    private final List<String> extensions;

    FileNamedType(final String type, final String... extensions) {
        this.type = type;
        this.extensions = Arrays.asList(extensions);
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public boolean has(final String extension) {
        return extensions.contains(extension.contains(".") ? extension.substring(1) : extension);
    }

    public static FileNamedType parse(final String extension) {
        final FileNamedType[] types = values();
        for (int index = 0; index < types.length; index++) {
            if (types[index].has(extension)) {
                return types[index];
            }
        }
        return null;
    }

}
