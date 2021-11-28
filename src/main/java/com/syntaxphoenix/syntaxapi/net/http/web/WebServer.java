package com.syntaxphoenix.syntaxapi.net.http.web;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import com.syntaxphoenix.syntaxapi.net.http.HttpSender;
import com.syntaxphoenix.syntaxapi.net.http.HttpServer;
import com.syntaxphoenix.syntaxapi.net.http.HttpWriter;
import com.syntaxphoenix.syntaxapi.net.http.NamedAnswer;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;
import com.syntaxphoenix.syntaxapi.net.http.RequestExecution;
import com.syntaxphoenix.syntaxapi.net.http.ResponseCode;
import com.syntaxphoenix.syntaxapi.net.http.StandardNamedType;

public class WebServer extends HttpServer {

    private WebHandler handler;

    public WebServer() {
        super();
    }

    public WebServer(ThreadFactory factory) {
        super(factory);
    }

    public WebServer(ExecutorService service) {
        super(service);
    }

    public WebServer(ThreadFactory factory, ExecutorService service) {
        super(factory, service);
    }

    public WebServer(int port) {
        super(port);
    }

    public WebServer(int port, ThreadFactory factory) {
        super(port, factory);
    }

    public WebServer(int port, ExecutorService service) {
        super(port, service);
    }

    public WebServer(int port, ThreadFactory factory, ExecutorService service) {
        super(port, factory, service);
    }

    public WebServer(int port, InetAddress address) {
        super(port, address);
    }

    public WebServer(int port, InetAddress address, ThreadFactory factory) {
        super(port, address, factory);
    }

    public WebServer(int port, InetAddress address, ExecutorService service) {
        super(port, address, service);
    }

    public WebServer(int port, InetAddress address, ThreadFactory factory, ExecutorService service) {
        super(port, address, factory, service);
    }

    /*
     * Getter
     */

    public WebHandler getHandler() {
        return handler;
    }

    /*
     * Setter
     */

    public void setHandler(WebHandler handler) {
        this.handler = handler;
    }

    /*
     * Handle HttpRequest
     */

    @Override
    protected RequestExecution handleHttpRequest(HttpSender sender, HttpWriter writer, ReceivedRequest request) throws Exception {
        try {
            return RequestExecution.of(handler.handleRequest(sender, writer, request));
        } catch (Exception e) {
            new NamedAnswer(StandardNamedType.PLAIN).setResponse("Something went wrong handling your request!")
                .code(ResponseCode.INTERNAL_SERVER_ERROR).write(writer);
            throw e;
        }
    }

}
