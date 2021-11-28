package com.syntaxphoenix.syntaxapi.net.http;

import java.util.HashMap;
import java.util.Iterator;

import com.syntaxphoenix.syntaxapi.utils.net.SimpleConversion;
import com.syntaxphoenix.syntaxapi.utils.net.ValueIdentifier;

public class ReceivedRequest {

    protected final HashMap<String, Object> headers = new HashMap<>();
    protected final HashMap<String, Object> cookies = new HashMap<>();
    protected final HashMap<String, Object> parameters = new HashMap<>();

    protected final RequestType type;
    protected final String[] path;
    protected final String fullPath;

    private RequestData<?> data;

    public ReceivedRequest(RequestType type, String[] path) {
        this.type = type;
        this.path = path;
        this.fullPath = SimpleConversion.toPath(path);
    }

    ReceivedRequest(RequestType type, String fullPath) {
        this.type = type;
        this.path = SimpleConversion.fromPath(fullPath);
        this.fullPath = fullPath;
    }

    /*
     * Parse parameters
     */

    public final ReceivedRequest parseParameters(String... parameters) {
        for (int index = 0; index < parameters.length; index++) {
            parseParameter(parameters[index].trim());
        }
        return this;
    }

    public final ReceivedRequest parseParameter(String parameter) {
        if (!parameter.contains("=")) {
            return this;
        }
        String[] array = parameter.split("=", 2);
        parameters.put(array[0], ValueIdentifier.identify(array[1]));
        return this;
    }

    /*
     * Parameters
     */

    public final HashMap<String, Object> getParams() {
        return parameters;
    }

    public final Object getParam(String key) {
        return parameters.get(key);
    }

    public final boolean hasParam(String key) {
        return parameters.containsKey(key);
    }

    /*
     * Parse cookies
     */

    public final ReceivedRequest parseCookies(String cookies) {
        if (!cookies.contains(";")) {
            return parseCookie(cookies.trim());
        }
        String[] array = cookies.split(";");
        for (int index = 0; index < array.length; index++) {
            parseCookie(array[index].trim());
        }
        return this;
    }

    public final ReceivedRequest parseCookie(String cookie) {
        if (!cookie.contains("=")) {
            return this;
        }
        String[] array = cookie.split("=");
        cookies.put(array[0], ValueIdentifier.identify(array[1]));
        return this;
    }

    /*
     * Cookies
     */

    public final HashMap<String, Object> getCookies() {
        return cookies;
    }

    public final Object getCookie(String key) {
        return cookies.get(key);
    }

    public final boolean hasCookie(String key) {
        return cookies.containsKey(key);
    }

    /*
     * Parse headers
     */

    public final ReceivedRequest parseHeaders(Iterable<String> headers) {
        return parseHeaders(headers.iterator());
    }

    public final ReceivedRequest parseHeaders(Iterator<String> headers) {
        while (headers.hasNext()) {
            parseHeader(headers.next());
        }
        return this;
    }

    public final ReceivedRequest parseHeaders(String... headers) {
        if (headers == null || headers.length == 0) {
            return this;
        }
        for (String header : headers) {
            parseHeader(header);
        }
        return this;
    }

    public final ReceivedRequest parseHeader(String header) {
        if (!header.contains(":")) {
            return this;
        }
        String[] parts = header.split(":", 2);
        if ((parts[0] = parts[0].toLowerCase()).equalsIgnoreCase("Cookie")) {
            return parseCookies(parts[1]);
        }
        headers.put(parts[0].toLowerCase(), ValueIdentifier.identify(parts[1].trim()));
        return this;
    }

    /*
     * Headers
     */

    public final HashMap<String, Object> getHeaders() {
        return headers;
    }

    public final Object getHeader(String key) {
        return headers.get(key.toLowerCase());
    }

    public final boolean hasHeader(String key) {
        return headers.containsKey(key.toLowerCase());
    }

    /*
     * Setter
     */

    public final ReceivedRequest setData(RequestData<?> data) {
        this.data = data;
        return this;
    }

    /*
     * Getter
     */

    public final RequestData<?> getData() {
        return data;
    }

    public final RequestType getType() {
        return type;
    }

    public final String[] getPath() {
        return path;
    }

    public final String getPathAsString() {
        return fullPath;
    }

}
