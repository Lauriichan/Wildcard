package me.lauriichan.minecraft.wildcard.core.util.source;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

import me.lauriichan.minecraft.wildcard.core.util.Resources;

public final class PathSource extends DataSource {

    private final Path path;

    public PathSource(Path path) {
        this.path = path;
    }

    @Override
    public boolean exists() {
        return Files.exists(path);
    }

    @Override
    public Path getSource() {
        return path;
    }

    @Override
    public InputStream openStream() throws IOException {
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    public static PathSource ofResource(String rawPath) {
        try {
            return new PathSource(Objects.requireNonNull(Resources.getInternalRoot().resolveSibling(rawPath)));
        } catch (Exception exp) {
            System.err.println("Failed to load Resource '" + rawPath + "'!");
            System.err.println(Exceptions.stackTraceToString(exp));
        }
        return null;
    }

}
