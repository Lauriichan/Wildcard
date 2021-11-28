package com.syntaxphoenix.syntaxapi.net.http;

import static com.syntaxphoenix.syntaxapi.net.http.RequestValidator.DEFAULT_VALIDATOR;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class RestApiServer extends HttpServer {

    protected RequestHandler handler;

    public RestApiServer() {
        super();
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(ThreadFactory factory) {
        super(factory);
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(ExecutorService service) {
        super(service);
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(ThreadFactory factory, ExecutorService service) {
        super(factory, service);
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(int port) {
        super(port);
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(int port, ThreadFactory factory) {
        super(port, factory);
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(int port, ExecutorService service) {
        super(port, service);
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(int port, ThreadFactory factory, ExecutorService service) {
        super(port, factory, service);
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(int port, InetAddress address) {
        super(port, address);
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(int port, InetAddress address, ThreadFactory factory) {
        super(port, address, factory);
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(int port, InetAddress address, ExecutorService service) {
        super(port, address, service);
        setValidator(DEFAULT_VALIDATOR);
    }

    public RestApiServer(int port, InetAddress address, ThreadFactory factory, ExecutorService service) {
        super(port, address, factory, service);
        setValidator(DEFAULT_VALIDATOR);
    }

    /*
     * Getter
     */

    public RequestHandler getHandler() {
        return handler;
    }

    /*
     * Setter
     */

    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }

    /*
     * Handle HttpRequest
     */

    @Override
    protected RequestExecution handleHttpRequest(HttpSender sender, HttpWriter writer, ReceivedRequest request) throws Exception {

        if (handler == null) {
            new NamedAnswer(StandardNamedType.PLAIN).setResponse("No message handler was registered, Sorry!")
                .code(ResponseCode.INTERNAL_SERVER_ERROR).write(writer);
            return RequestExecution.error(new IllegalStateException("Handler can't be null!"));
        }

        try {
            return RequestExecution.of(handler.handleRequest(sender, writer, request));
        } catch (Exception e) {
            new NamedAnswer(StandardNamedType.PLAIN).setResponse("Something went wrong handling your request!")
                .code(ResponseCode.INTERNAL_SERVER_ERROR).write(writer);
            throw e;
        }
    }

}
