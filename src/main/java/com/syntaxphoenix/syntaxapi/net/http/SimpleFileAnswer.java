package com.syntaxphoenix.syntaxapi.net.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.syntaxphoenix.syntaxapi.utils.java.Streams;

public class SimpleFileAnswer extends FileAnswer {

    public SimpleFileAnswer(File file, NamedType type) {
        super(file, type);
    }

    @Override
    public String readFile() {
        try {
            return Streams.toString(new FileInputStream(file));
        } catch (IOException e) {
            return "";
        }
    }

}
