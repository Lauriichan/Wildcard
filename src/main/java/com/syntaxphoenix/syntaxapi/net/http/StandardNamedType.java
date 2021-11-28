package com.syntaxphoenix.syntaxapi.net.http;

import java.util.Arrays;
import java.util.List;

public enum StandardNamedType implements NamedType {

    PLAIN("text/plain", "txt"),
    JAVA_SCRIPT("text/javascript", "js"),
    XML("text/xml", "xml"),
    CSS("text/css", "css"),
    HTML("text/html", "htm", "html", "shtml");

    private final String type;
    private final List<String> extensions;

    private StandardNamedType(String type, String... extensions) {
        this.type = type;
        this.extensions = Arrays.asList(extensions);
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public boolean has(String extension) {
        return extensions.contains(extension.contains(".") ? extension.substring(1) : extension);
    }

    public static StandardNamedType parse(String extension) {
        StandardNamedType[] types = values();
        for (int index = 0; index < types.length; index++) {
            if (types[index].has(extension)) {
                return types[index];
            }
        }
        return null;
    }

}
