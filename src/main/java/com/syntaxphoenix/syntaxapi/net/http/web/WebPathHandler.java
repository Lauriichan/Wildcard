package com.syntaxphoenix.syntaxapi.net.http.web;

import java.io.File;

import com.syntaxphoenix.syntaxapi.net.http.HttpSender;
import com.syntaxphoenix.syntaxapi.net.http.HttpWriter;
import com.syntaxphoenix.syntaxapi.net.http.NamedAnswer;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;
import com.syntaxphoenix.syntaxapi.net.http.ResponseCode;
import com.syntaxphoenix.syntaxapi.net.http.SimpleFileAnswer;
import com.syntaxphoenix.syntaxapi.net.http.StandardNamedType;

public class WebPathHandler implements IWebPathHandler {

    @Override
    public void handlePath(File directory, HttpSender sender, HttpWriter writer, ReceivedRequest data) throws Exception {
        File file = new File(directory, data.getPathAsString());
        if (!file.exists()) {
            new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.NOT_FOUND).write(writer);
            return;
        }

        if (!file.isFile()) {
            file = new File(file, "index.html");
            if (!file.exists()) {
                new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.NOT_FOUND).write(writer);
                return;
            }
            new SimpleFileAnswer(file, StandardNamedType.HTML).code(ResponseCode.OK).write(writer);
            return;
        }

        StandardNamedType type = StandardNamedType.parse(data.getPath()[data.getPath().length - 1]);
        if (type == null) {
            new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.NOT_FOUND).write(writer);
            return;
        }

        new SimpleFileAnswer(file, type).code(ResponseCode.OK).write(writer);

    }

}
