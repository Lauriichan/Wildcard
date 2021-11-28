package com.syntaxphoenix.syntaxapi.net.http;

import java.io.InputStreamReader;
import java.net.Socket;

public class HttpSender {

    private final Socket client;
    private final InputStreamReader input;

    public HttpSender(Socket client, InputStreamReader input) {
        this.client = client;
        this.input = input;
    }

    public Socket getClient() {
        return client;
    }

    public InputStreamReader getInput() {
        return input;
    }

}
