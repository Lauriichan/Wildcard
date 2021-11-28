package com.syntaxphoenix.syntaxapi.net.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import com.syntaxphoenix.syntaxapi.net.AsyncSocketServer;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

public abstract class HttpServer extends AsyncSocketServer {

    protected final HashSet<RequestType> supported = new HashSet<>();

    protected RequestGate gate;
    protected RequestValidator validator;
    protected RequestSerializer serializer;

    public HttpServer() {
        super();
    }

    public HttpServer(ThreadFactory factory) {
        super(factory);
    }

    public HttpServer(ExecutorService service) {
        super(service);
    }

    public HttpServer(ThreadFactory factory, ExecutorService service) {
        super(factory, service);
    }

    public HttpServer(int port) {
        super(port);
    }

    public HttpServer(int port, ThreadFactory factory) {
        super(port, factory);
    }

    public HttpServer(int port, ExecutorService service) {
        super(port, service);
    }

    public HttpServer(int port, ThreadFactory factory, ExecutorService service) {
        super(port, factory, service);
    }

    public HttpServer(int port, InetAddress address) {
        super(port, address);
    }

    public HttpServer(int port, InetAddress address, ThreadFactory factory) {
        super(port, address, factory);
    }

    public HttpServer(int port, InetAddress address, ExecutorService service) {
        super(port, address, service);
    }

    public HttpServer(int port, InetAddress address, ThreadFactory factory, ExecutorService service) {
        super(port, address, factory, service);
    }

    /*
     * Getter
     */

    public RequestGate getGate() {
        return gate;
    }

    public RequestValidator getValidator() {
        return validator;
    }

    public RequestSerializer getSerializer() {
        return serializer;
    }

    /*
     * Setter
     */

    public HttpServer setGate(RequestGate gate) {
        this.gate = gate;
        return this;
    }

    public HttpServer setValidator(RequestValidator validator) {
        this.validator = validator;
        return this;
    }

    public void setSerializer(RequestSerializer serializer) {
        this.serializer = serializer;
    }

    /*
     * RequestType management
     */

    public HttpServer addType(RequestType type) {
        supported.add(type);
        return this;
    }

    public HttpServer addTypes(RequestType... types) {
        for (int index = 0; index < types.length; index++) {
            supported.add(types[index]);
        }
        return this;
    }

    public HttpServer removeType(RequestType type) {
        supported.remove(type);
        return this;
    }

    public HttpServer removeTypes(RequestType... types) {
        for (int index = 0; index < types.length; index++) {
            supported.remove(types[index]);
        }
        return this;
    }

    public HttpServer clearTypes() {
        supported.clear();
        return this;
    }

    public RequestType[] getTypes() {
        return supported.toArray(new RequestType[0]);
    }

    /*
     * Handle clients
     */

    @Override
    protected void handleClientAsync(Socket socket) throws Throwable {

        HttpWriter writer = new HttpWriter(new PrintStream(socket.getOutputStream()));

        InputStream stream = new BufferedInputStream(socket.getInputStream());
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

        String line = readLine(stream, reader);

        if (line == null) {
            new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.NO_CONTENT).write(writer);
            socket.close();
            return;
        }

        String[] info = line.split(" ");
        String[] path = info[1].startsWith("/") ? info[1].substring(1).split("/") : info[1].split("/");
        String[] parameters = null;

        if (path[path.length - 1].contains("?")) {
            parameters = path[path.length - 1].split("\\?");
            path[path.length - 1] = parameters[0];
            parameters = parameters[1].contains("&") ? parameters[1].split("&")
                : new String[] {
                    parameters[1]
                };
        }

        ReceivedRequest request = new ReceivedRequest(RequestType.fromString(info[0]), path);

        if (!supported.isEmpty() && !supported.contains(request.getType())) {
            new NamedAnswer(StandardNamedType.PLAIN).setResponse("Unsupported request method!").code(ResponseCode.BAD_REQUEST)
                .write(writer);
            reader.close();
            writer.close();
            socket.close();
            return;
        }

        if (parameters != null) {
            request.parseParameters(parameters);
        }

        while ((line = readLine(stream, reader)) != null) {
            if (line.isBlank()) {
                break;
            }
            request.parseHeader(line);
        }
        stream.reset();

        if (gate != null) {
            RequestState state = gate.acceptRequest(writer, request);
            if (state.accepted()) {
                if (request.hasHeader("expect")) {
                    if (((String) request.getHeader("expect")).contains("100-continue")) {
                        new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.CONTINUE).write(writer);
                    }
                }
            } else {
                if (!state.message()) {
                    new NamedAnswer(StandardNamedType.PLAIN).setResponse("Method or contenttype is not supported")
                        .code(ResponseCode.BAD_REQUEST).write(writer);
                }
                reader.close();
                writer.close();
                socket.close();
                return;
            }
        } else {
            if (request.hasHeader("expect")) {
                if (((String) request.getHeader("expect")).contains("100-continue")) {
                    new NamedAnswer(StandardNamedType.PLAIN).setResponse("No content length given!").code(ResponseCode.LENGTH_REQUIRED)
                        .write(writer);
                }
            }
        }

        /*
         * 
         */

        RequestContent content = RequestContent.UNNEEDED;
        if (validator != null) {
            content = validator.parseContent(writer, request);
        }

        if (!content.ignore()) {

            if (content.message()) {
                new NamedAnswer(StandardNamedType.PLAIN).setResponse("No content length given!").code(ResponseCode.LENGTH_REQUIRED)
                    .write(writer);
                reader.close();
                writer.close();
                socket.close();
                return;
            }

            int length = ((Number) request.getHeader("Content-Length")).intValue();

            ByteArrayOutputStream array = new ByteArrayOutputStream();
            
            new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.RESET_CONTENT).write(writer);
            
            System.out.println(stream.available());
            
            
            int remain = length;
            while (--remain != 0) {
                array.write(stream.read());
            }
            byte[] data = array.toByteArray();
            if (serializer != null) {
                try {
                    request.setData(serializer.serialize(data));
                } catch (Exception exp) {
                    System.out.println(Exceptions.stackTraceToString(exp));
                    new NamedAnswer(StandardNamedType.PLAIN).setResponse("Failed to serialize data")
                        .code(ResponseCode.INTERNAL_SERVER_ERROR).write(writer);
                    reader.close();
                    writer.close();
                    socket.close();
                    return;
                }
            } else {
                request.setData(new CustomRequestData<>(String.class, new String(data, StandardCharsets.UTF_8)));
            }
        }

        RequestExecution execution = null;
        try {
            execution = handleHttpRequest(new HttpSender(socket, reader), writer, request);
        } catch (Exception e) {
            execution = RequestExecution.error(e);
        }

        if ((execution == null ? RequestExecution.CLOSE : execution).close()) {
            reader.close();
            writer.close();
            socket.close();
            if (execution.hasThrowable()) {
                throw execution.getThrowable();
            }
        }

    }
    
    private String readLine(InputStream stream, InputStreamReader reader) throws IOException {
        stream.mark(8192);
        int character;
        StringBuilder builder = new StringBuilder();
        while ((character = reader.read()) != '\r' && character != -1) {
            if(character == '\n') {
                continue;
            }
            builder.append((char) character);
        }
        if(stream.available() > 0) {
            stream.skip(1);
        }
        return builder.toString();
    }

    protected RequestExecution handleHttpRequest(HttpSender sender, HttpWriter writer, ReceivedRequest request) throws Exception {
        return RequestExecution.CLOSE;
    }

}
