package com.syntaxphoenix.syntaxapi.net.http.web;

import java.io.File;

import com.syntaxphoenix.syntaxapi.net.http.PathHandler;
import com.syntaxphoenix.syntaxapi.net.http.RedirectRequestHandler;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public class WebRedirectHandler extends RedirectRequestHandler implements WebHandler {

    private final Container<File> directory = Container.of();

    public WebRedirectHandler(File file) {
        directory.replace(file);
    }

    public WebRedirectHandler setDirectory(File directory) {
        this.directory.replace(directory);
        return this;
    }

    public Container<File> getDirectory() {
        return directory;
    }

    public WebRedirectHandler set(IWebPathHandler pathHandler, String... path) {
        set(new WebPathAdapter(directory, pathHandler), path);
        return this;
    }

    public WebRedirectHandler set(WebPathAdapter pathAdapter, String... path) {
        set(pathAdapter, path);
        return this;
    }

    public WebRedirectHandler set(String path, IWebPathHandler pathHandler) {
        set(path, new WebPathAdapter(directory, pathHandler));
        return this;
    }

    public WebRedirectHandler set(String path, WebPathAdapter pathAdapter) {
        set(path, (PathHandler) pathAdapter);
        return this;
    }

    public WebRedirectHandler setDefault(IWebPathHandler pathHandler) {
        setDefault(new WebPathAdapter(directory, pathHandler));
        return this;
    }

    public WebRedirectHandler setDefault(WebPathAdapter pathAdapter) {
        setDefault((PathHandler) pathAdapter);
        return this;
    }

}
