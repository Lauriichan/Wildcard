package com.syntaxphoenix.syntaxapi.net.http;

import java.util.HashMap;
import java.util.Objects;

import com.syntaxphoenix.syntaxapi.utils.java.Arrays;
import com.syntaxphoenix.syntaxapi.utils.net.SimpleConversion;

public class RedirectRequestHandler implements RequestHandler, PathHandler {

    private final HashMap<String, PathHandler> paths = new HashMap<>();
    private PathHandler main;

    @Override
    public boolean handleRequest(HttpSender sender, HttpWriter writer, ReceivedRequest data) throws Exception {
        redirectPath(sender, writer, data);
        return true;
    }

    @Override
    public void handlePath(HttpSender sender, HttpWriter writer, ReceivedRequest data) throws Exception {
        if (data.getPath().length == 1) {
            if (main == null) {
                new NamedAnswer(StandardNamedType.PLAIN).setResponse("Not Found").code(ResponseCode.NOT_FOUND).write(writer);
                return;
            }
            main.handlePath(sender, writer, data);
            return;
        }
        redirectPath(sender, writer, new PathedReceivedRequest(data));
    }

    /*
     * Manage paths
     */

    public RedirectRequestHandler setDefault(PathHandler handler) {
        this.main = handler;
        return this;
    }

    public RedirectRequestHandler set(String path, PathHandler handler) {
        Objects.requireNonNull(path, "Path cannot be null!");
        if (handler == null) {
            paths.remove(path);
            return this;
        }
        paths.put(path, handler);
        return this;
    }

    public RedirectRequestHandler set(PathHandler handler, String... path) {
        return set(path(path), handler);
    }

    public RedirectRequestHandler delete(String... path) {
        return set(path(path), null);
    }

    public PathHandler getDefault() {
        return main;
    }

    public PathHandler get(String... path) {
        return paths.get(path(path));
    }

    protected String path(String... path) {
        return Objects.requireNonNull(path, "Path cannot be null!").length == 1 ? path[0] : SimpleConversion.toPath(path);
    }

    /*
     * Redirect to correct path
     */

    protected void redirectPath(HttpSender sender, HttpWriter writer, ReceivedRequest data) throws Exception {
        PathHandler handler = paths.get(data.getPathAsString());
        if (handler == null) {
            if (main == null) {
                new NamedAnswer(StandardNamedType.PLAIN).setResponse("Not Found").code(ResponseCode.NOT_FOUND).write(writer);
                return;
            }
            main.handlePath(sender, writer, data);
            return;
        }
        handler.handlePath(sender, writer, data);
    }

    /*
     * Path conversion class
     */

    public static class PathedReceivedRequest extends ReceivedRequest {

        public PathedReceivedRequest(ReceivedRequest request) {
            this(Arrays.subArray(size -> new String[size], request.getPath(), 1), request);
        }

        public PathedReceivedRequest(String path, ReceivedRequest request) {
            super(request.getType(), path);
            getHeaders().putAll(request.getHeaders());
            setData(request.getData());
        }

        public PathedReceivedRequest(String path[], ReceivedRequest request) {
            super(request.getType(), path);
            getHeaders().putAll(request.getHeaders());
            setData(request.getData());
        }

    }

}
