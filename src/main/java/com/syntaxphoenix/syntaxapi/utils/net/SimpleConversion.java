package com.syntaxphoenix.syntaxapi.utils.net;

public final class SimpleConversion {
    
    private SimpleConversion() {}

    public static String toPath(String[] path) {
        return toPath(path, "/");
    }

    public static String toPath(String[] path, String joiner) {
        if (path == null || path.length == 0) {
            return "";
        }
        if (path[0].isEmpty()) {
            return "";
        }
        return String.join(joiner, path);
    }

    public static String[] fromPath(String path) {
        return path == null ? new String[0] : path.split("/");
    }

}
