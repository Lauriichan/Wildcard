package com.syntaxphoenix.syntaxapi.net.http;

public enum RequestExecution {

    CLOSE(true),
    THROW(true),
    OPEN(false);

    private final boolean close;

    private RequestExecution(boolean close) {
        this.close = close;
    }

    private Throwable throwable;

    private RequestExecution setThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public boolean hasThrowable() {
        return throwable != null;
    }

    public boolean close() {
        return close;
    }

    public static RequestExecution of(boolean close) {
        return close ? CLOSE : OPEN;
    }

    public static RequestExecution error(Throwable throwable) {
        return RequestExecution.THROW.setThrowable(throwable);
    }

}
