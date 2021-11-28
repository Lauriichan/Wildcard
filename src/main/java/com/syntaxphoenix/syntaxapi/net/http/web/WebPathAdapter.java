package com.syntaxphoenix.syntaxapi.net.http.web;

import java.io.File;

import com.syntaxphoenix.syntaxapi.net.http.HttpSender;
import com.syntaxphoenix.syntaxapi.net.http.HttpWriter;
import com.syntaxphoenix.syntaxapi.net.http.PathHandler;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public class WebPathAdapter implements PathHandler {

    private final Container<File> directory;
    private final IWebPathHandler handler;

    public WebPathAdapter(Container<File> directory, IWebPathHandler handler) {
        this.directory = directory;
        this.handler = handler;
    }

    @Override
    public final void handlePath(HttpSender sender, HttpWriter writer, ReceivedRequest data) throws Exception {
        handler.handlePath(directory.get(), sender, writer, data);
    }

}
