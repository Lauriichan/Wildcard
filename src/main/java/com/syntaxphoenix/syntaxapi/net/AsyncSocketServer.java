package com.syntaxphoenix.syntaxapi.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class AsyncSocketServer extends SocketServer {

    private final ExecutorService service;

    public AsyncSocketServer() {
        this((ThreadFactory) null);
    }

    public AsyncSocketServer(ThreadFactory factory) {
        this(factory, Executors.newCachedThreadPool(factory));
    }

    public AsyncSocketServer(ExecutorService service) {
        this(DEFAULT_PORT, service);
    }

    public AsyncSocketServer(ThreadFactory factory, ExecutorService service) {
        this(DEFAULT_PORT, factory, service);
    }

    public AsyncSocketServer(int port) {
        this(port, (ThreadFactory) null);
    }

    public AsyncSocketServer(int port, ThreadFactory factory) {
        this(port, factory, Executors.newCachedThreadPool(factory));
    }

    public AsyncSocketServer(int port, ExecutorService service) {
        super(port);
        this.service = service;
    }

    public AsyncSocketServer(int port, ThreadFactory factory, ExecutorService service) {
        super(port, factory);
        this.service = service;
    }

    public AsyncSocketServer(int port, InetAddress address) {
        this(port, address, (ThreadFactory) null);
    }

    public AsyncSocketServer(int port, InetAddress address, ThreadFactory factory) {
        this(port, address, factory, factory == null ? Executors.newCachedThreadPool() : Executors.newCachedThreadPool(factory));
    }

    public AsyncSocketServer(int port, InetAddress address, ExecutorService service) {
        super(port, address);
        this.service = service;
    }

    public AsyncSocketServer(int port, InetAddress address, ThreadFactory factory, ExecutorService service) {
        super(port, address, factory);
        this.service = service;
    }

    /*
     * 
     */

    public final ExecutorService getExecutorSerivce() {
        return service;
    }

    /*
     * 
     */

    @Override
    protected void handleClient(Socket socket) throws Throwable {
        service.execute(() -> {
            try {
                handleClientAsync(socket);
            } catch (Throwable throwable) {
                try {
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e) {
                }
                handleExceptionAsync(throwable);
            }
        });
    }

    protected void handleExceptionAsync(Throwable throwable) {
        throwable.printStackTrace();
    }

    protected abstract void handleClientAsync(Socket socket) throws Throwable;

}
