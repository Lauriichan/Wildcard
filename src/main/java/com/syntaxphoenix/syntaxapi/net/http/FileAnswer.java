package com.syntaxphoenix.syntaxapi.net.http;

import java.io.File;

public abstract class FileAnswer extends NamedAnswer implements Refreshable<FileAnswer> {

    protected final File file;

    public FileAnswer(File file, NamedType type) {
        super(type);
        this.file = file;
        refresh();
    }

    @Override
    public FileAnswer setResponse(String response) {
        return this;
    }

    public File getFile() {
        return file;
    }

    @Override
    public FileAnswer refresh() {
        this.response = readFile();
        return this;
    }

    protected abstract String readFile();

}
