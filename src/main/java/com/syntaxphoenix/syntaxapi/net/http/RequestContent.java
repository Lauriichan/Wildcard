package com.syntaxphoenix.syntaxapi.net.http;

public enum RequestContent {

    UNNEEDED,
    NEEDED;

    private boolean message = false;

    public boolean message() {
        return message;
    }

    public RequestContent message(boolean message) {
        this.message = message;
        return this;
    }

    public boolean ignore() {
        return this == UNNEEDED;
    }

    public boolean accepted() {
        return this == NEEDED && !message();
    }

    public boolean denied() {
        return this == NEEDED && message();
    }

}
