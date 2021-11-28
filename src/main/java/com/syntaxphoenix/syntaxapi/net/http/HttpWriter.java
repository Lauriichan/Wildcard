package com.syntaxphoenix.syntaxapi.net.http;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpWriter {

    private final PrintStream output;

    public HttpWriter(PrintStream output) {
        this.output = output;
    }

    /*
     * 
     */

    public PrintStream getStream() {
        return output;
    }

    /*
     * 
     */

    public HttpWriter write(int code) {
        getStream().println("HTTP/1.1 " + code + ' ' + ResponseCode.getName(code));
        return this;
    }

    /*
     * 
     */

    public HttpWriter writeServer() {
        return write("Server", "Java SyntaxPhoenix HTTP Server from Lauriichan : 1.0");
    }

    public HttpWriter writeDate() {
        return write("Date", new Date());
    }

    public HttpWriter writeLength(int length) {
        return write("Content-Length", length);
    }

    public HttpWriter writeType(ContentType type) {
        return writeType(type.type());
    }

    public HttpWriter writeType(String type) {
        return write("Content-Type", type + "; charset=UTF-8");
    }

    public HttpWriter writeCookies(Cookie... cookies) {
        for (int index = 0; index < cookies.length; index++) {
            writeCookie(cookies[index]);
        }
        return this;
    }

    public HttpWriter writeCookie(Cookie cookie) {
        StringBuilder builder = new StringBuilder();
        builder.append(cookie.getName());
        builder.append('=');
        builder.append(cookie.getValue());
        HashMap<String, Object> properties = cookie.getProperties();
        if (!properties.isEmpty()) {
            synchronized (properties) {
                builder.append("; ");
                builder.append(properties.entrySet().stream()
                    .map(entry -> entry.getValue() == null ? entry.getKey() : entry.getKey() + '=' + entry.getValue())
                    .collect(Collectors.joining("; ")));
            }
        }
        return write("Set-Cookie", builder.toString());
    }

    /*
     * 
     */

    public <T extends Object> HttpWriter write(Entry<String, T> entry) {
        return write(entry.getKey(), entry.getValue());
    }

    public HttpWriter write(String key, Object value) {
        return write(key, value.toString());
    }

    public HttpWriter write(String key, String value) {
        getStream().println(key + ": " + value);
        return this;
    }

    /*
     * 
     */

    public HttpWriter write(byte[] buffer) throws IOException {
        getStream().write(buffer);
        return this;
    }

    public HttpWriter line() {
        getStream().println();
        return this;
    }

    /*
     * 
     */

    public HttpWriter clear() {
        getStream().flush();
        return this;
    }

    public HttpWriter close() {
        getStream().close();
        return this;
    }

}
