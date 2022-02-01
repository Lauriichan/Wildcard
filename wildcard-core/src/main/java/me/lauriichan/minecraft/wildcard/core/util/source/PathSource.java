package me.lauriichan.minecraft.wildcard.core.util.source;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Objects;

import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public final class PathSource extends DataSource {

    private static final Container<Path> ROOT = Container.of();

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
            return new PathSource(Objects.requireNonNull(getClasspath().resolveSibling(rawPath)));
        } catch (Exception exp) {
            System.err.println("Failed to load Resource '" + rawPath + "'!");
            System.err.println(Exceptions.stackTraceToString(exp));
        }
        return null;
    }

    public static Path getClasspath() {
        if (ROOT.isPresent()) {
            return ROOT.get();
        }
        try {
            URI uri = PathSource.class.getResource("/").toURI();
            Path path = uri.getScheme().equals("jar") ? FileSystems.newFileSystem(uri, Collections.emptyMap()).getPath("/")
                : Paths.get(uri).resolve("classes");
            ROOT.replace(path).lock();
            return ROOT.get();
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException("Failed to retrieve classpath", e);
        }
    }

}
