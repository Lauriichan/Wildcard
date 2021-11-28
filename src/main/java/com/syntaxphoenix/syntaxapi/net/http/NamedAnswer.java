package com.syntaxphoenix.syntaxapi.net.http;

public class NamedAnswer extends Answer<String> {

    protected String response;

    public NamedAnswer(NamedType type) {
        super(type);
    }

    @Override
    public boolean hasResponse() {
        return response != null;
    }

    @Override
    public String getResponse() {
        return response;
    }

    public NamedAnswer setResponse(String response) {
        this.response = response;
        return this;
    }

    @Override
    public NamedAnswer clearResponse() {
        this.response = null;
        return this;
    }

    @Override
    public byte[] serializeResponse() {
        return serializeString(response);
    }

}
