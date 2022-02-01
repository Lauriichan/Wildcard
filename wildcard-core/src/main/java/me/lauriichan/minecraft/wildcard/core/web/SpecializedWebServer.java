package me.lauriichan.minecraft.wildcard.core.web;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import com.syntaxphoenix.syntaxapi.net.http.web.WebServer;

public class SpecializedWebServer extends WebServer {

    public SpecializedWebServer() {
        super();
    }

    public SpecializedWebServer(ThreadFactory factory) {
        super(factory);
    }

    public SpecializedWebServer(ExecutorService service) {
        super(service);
    }

    public SpecializedWebServer(ThreadFactory factory, ExecutorService service) {
        super(factory, service);
    }

    public SpecializedWebServer(int port) {
        super(port);
    }

    public SpecializedWebServer(int port, ThreadFactory factory) {
        super(port, factory);
    }

    public SpecializedWebServer(int port, ExecutorService service) {
        super(port, service);
    }

    public SpecializedWebServer(int port, ThreadFactory factory, ExecutorService service) {
        super(port, factory, service);
    }

    public SpecializedWebServer(int port, InetAddress address) {
        super(port, address);
    }

    public SpecializedWebServer(int port, InetAddress address, ThreadFactory factory) {
        super(port, address, factory);
    }

    public SpecializedWebServer(int port, InetAddress address, ExecutorService service) {
        super(port, address, service);
    }

    public SpecializedWebServer(int port, InetAddress address, ThreadFactory factory, ExecutorService service) {
        super(port, address, factory, service);
    }
    
    @Override
    protected void handleException(Throwable throwable) {
        // Just ignore
    }
    
    @Override
    protected void handleExceptionAsync(Throwable throwable) {
        // Just ignore
    }

}
