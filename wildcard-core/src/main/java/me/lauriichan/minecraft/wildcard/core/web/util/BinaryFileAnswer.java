package me.lauriichan.minecraft.wildcard.core.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.syntaxphoenix.syntaxapi.net.http.Answer;
import com.syntaxphoenix.syntaxapi.net.http.NamedType;
import com.syntaxphoenix.syntaxapi.net.http.Refreshable;
import com.syntaxphoenix.syntaxapi.utils.java.Streams;

public final class BinaryFileAnswer extends Answer<byte[]> implements Refreshable<BinaryFileAnswer> {

    protected final File file;

    private byte[] response;

    public BinaryFileAnswer(File file, NamedType type) {
        super(type);
        this.file = file;
        refresh();
    }

    @Override
    public boolean hasResponse() {
        return response != null;
    }

    @Override
    public byte[] getResponse() {
        return response;
    }

    @Override
    public BinaryFileAnswer clearResponse() {
        response = null;
        return this;
    }

    @Override
    public byte[] serializeResponse() {
        return response;
    }

    @Override
    public BinaryFileAnswer refresh() {
        try {
            response = Streams.toByteArray(new FileInputStream(file));
        } catch (IOException e) {
            response = null;
        }
        return this;
    }

}
