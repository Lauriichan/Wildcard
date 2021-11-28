package com.syntaxphoenix.syntaxapi.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;

import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.utils.java.Streams;

public class Request {

    private final HashMap<String, String> headers = new HashMap<>();
    private final RequestType request;

    private JsonObject parameters = new JsonObject();
    private boolean modifyUrl = false;

    /*
     * 
     */

    public Request(RequestType request) {
        this.request = request;
    }

    /*
     * 
     */

    public Request modifyUrl(boolean modifyUrl) {
        this.modifyUrl = modifyUrl;
        return this;
    }

    public Request header(String key, String value) {
        if (value != null) {
            headers.put(key, value);
        } else {
            headers.remove(key);
        }
        return this;
    }

    public Request parameter(String key, String value) {
        if (value != null) {
            parameters.set(key, value);
        } else {
            parameters.remove(key);
        }
        return this;
    }

    public Request parameter(String key, JsonValue<?> element) {
        if (element != null) {
            parameters.set(key, element);
        } else {
            parameters.remove(key);
        }
        return this;
    }

    public Request parameter(JsonObject object) {
        parameters = object;
        return this;
    }

    /*
     * 
     */

    public Request clearHeader() {
        headers.clear();
        return this;
    }

    public Request clearParameters() {
        parameters = new JsonObject();
        return this;
    }

    /*
     * 
     */

    public boolean doesModifyUrl() {
        return modifyUrl;
    }

    public boolean hasParameters() {
        return !parameters.isEmpty();
    }

    /*
     * 
     */

    public Response run(String url) throws IOException {
        return execute(url, null);
    }

    public Response run(URL url) throws IOException {
        return execute(url, null);
    }

    public Response execute(String url, ContentType content) throws IOException {
        return execute(new URL(url), content);
    }

    public Response execute(URL url, ContentType content) throws IOException {

        byte[] data = null;
        int length = 0;

        if (hasParameters() && request.hasOutput()) {
            CustomRequestData<JsonObject> requestData = new CustomRequestData<>(JsonObject.class, parameters);
            if (!modifyUrl) {
                data = content.serialize(requestData).getBytes(StandardCharsets.UTF_8);
                length = data.length;
            } else if (content.supportsUrlModification()) {
                content.modifyUrl(url, requestData);
            }
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(request.name());

        connection.setDoOutput(true);

        for (Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }

        if (request.hasOutput()) {
            connection.setRequestProperty("Content-Type", content.type() + "; charset=UTF-8");
            connection.setFixedLengthStreamingMode(length);
        }

        connection.connect();

        if (data != null && request.hasOutput()) {

            OutputStream output = connection.getOutputStream();
            output.write(data);
            output.flush();
            output.close();

        }

        InputStream stream = null;

        try {
            stream = connection.getInputStream();
            if (stream == null) {
                stream = connection.getErrorStream();
            }
        } catch (IOException ignore) {
            stream = connection.getErrorStream();
        }

        byte[] response = new byte[0];

        if (stream != null) {
            response = Streams.toByteArray(stream);
        }

        return new Response(connection.getResponseCode(), response, connection.getHeaderFields());

    }

}
