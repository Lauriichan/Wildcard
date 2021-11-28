package com.syntaxphoenix.syntaxapi.net.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public abstract class Answer<E> {

    public static final List<String> BLOCKED = Arrays.asList("Content-Type", "Server", "Date", "Content-Length", "Set-Cookie");

    protected final HashMap<String, String> headers = new HashMap<>();
    protected final ArrayList<Cookie> cookies = new ArrayList<>();
    protected final NamedType type;

    protected int code = 404;

    public Answer(NamedType type) {
        this.type = type;
    }

    /*
     * Getter
     */

    public String header(String key) {
        return headers.get(key);
    }

    public NamedType type() {
        return type;
    }

    public int code() {
        return code;
    }

    /*
     * Setter
     */

    public Answer<E> code(int code) {
        this.code = code;
        return this;
    }

    public Answer<E> header(String key, Object value) {
        return header(key, value.toString());
    }

    public Answer<E> header(String key, String value) {
        synchronized (headers) {
            if (value != null) {
                headers.put(key, value);
            } else {
                headers.remove(key);
            }
        }
        return this;
    }

    public Answer<E> addCookie(String key, Object value) {
        return addCookie(Cookie.of(key, value));
    }

    public Answer<E> addCookie(Cookie cookie) {
        synchronized (cookies) {
            if (!cookies.contains(cookie)) {
                cookies.add(cookie);
            }
        }
        return this;
    }

    public Answer<E> removeCookie(Cookie cookie) {
        synchronized (cookies) {
            cookies.remove(cookie);
        }
        return this;
    }

    public Answer<E> removeCookie(String key) {
        synchronized (cookies) {
            cookies.stream().filter(cookie -> cookie.getName().equals(key)).findFirst().ifPresent(cookie -> cookies.remove(cookie));
        }
        return this;
    }

    /*
     * Response Management
     */

    public Answer<E> clearHeaders() {
        synchronized (headers) {
            headers.clear();
        }
        return this;
    }

    public Answer<E> clearCookies() {
        synchronized (cookies) {
            cookies.clear();
        }
        return this;
    }

    public abstract boolean hasResponse();

    public abstract E getResponse();

    public abstract Answer<E> clearResponse();

    /*
     * Serialize answer
     */

    public abstract byte[] serializeResponse();

    protected byte[] serializeString(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    /*
     * Send answer
     */

    public Answer<E> write(HttpWriter writer) throws IOException {
        if (type == null) {
            return this;
        }

        byte[] data = null;
        int length = 0;

        if (hasResponse()) {

            data = serializeResponse();
            length = data.length;

        }

        writer.write(code).writeServer().writeDate().writeLength(length);

        Set<Entry<String, String>> headers;
        synchronized (this.headers) {
            headers = this.headers.entrySet();
        }
        for (Entry<String, String> header : headers) {
            if (BLOCKED.contains(header.getKey())) {
                continue;
            }
            writer.write(header);
        }

        Cookie[] cookie;
        synchronized (cookies) {
            cookie = cookies.toArray(new Cookie[0]);
        }
        writer.writeCookies(cookie).writeType(type.type());

        writer.line();

        if (data != null) {
            writer.write(data);
        }

        writer.clear();

        return this;
    }

}
