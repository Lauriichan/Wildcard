package com.syntaxphoenix.syntaxapi.net.http;

import java.nio.charset.StandardCharsets;

@FunctionalInterface
public interface RequestTextSerializer extends RequestSerializer {
    
    default RequestData<?> serialize(byte[] data) throws Exception {
        return serialize(new String(data, StandardCharsets.UTF_8));
    }

    RequestData<?> serialize(String data) throws Exception;

}
