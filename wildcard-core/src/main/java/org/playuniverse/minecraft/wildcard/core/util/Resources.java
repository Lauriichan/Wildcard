package org.playuniverse.minecraft.wildcard.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.file.PathUtils;
import org.playuniverse.minecraft.wildcard.core.WildcardCore;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;
import com.syntaxphoenix.syntaxapi.utils.java.Files;

public final class Resources {

    private final File folder;
    private final ILogger logger;
    private final Class<?> resourceClass;

    public Resources(final WildcardCore core) {
        this.logger = core.getLogger();
        this.folder = core.getPlugin().getDataFolder();
        this.resourceClass = core.getPlugin().getClass();
    }

    public Path getExternalPathForImpl(final String path) {
        try {
            final URI uri = resourceClass.getResource("/" + path).toURI();
            final Path root = "jar".equals(uri.getScheme()) ? getPathFor(uri, "/" + path) : Paths.get(uri);
            final File target = new File(folder, path);
            if (PathUtils.isDirectory(root)) {
                return createDirectoryPath(root, target);
            }
            return createFilePath(root, target);
        } catch (final Exception exp) {
            logger.log(LogTypeId.ERROR, "Failed to retrieve resource '" + path + "'!");
            logger.log(LogTypeId.ERROR, Exceptions.stackTraceToString(exp));
            return new File(folder, path).toPath();
        }
    }

    private Path getPathFor(final URI uri, final String path) throws IOException {
        try {
            return FileSystems.getFileSystem(uri).getPath(path);
        } catch (final Exception exp) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap()).getPath(path);
        }
    }

    private Path createDirectoryPath(final Path path, final File target) throws Exception {
        if (target.exists()) {
            return target.toPath();
        }
        Files.createFolder(target);
        try (Stream<Path> walk = java.nio.file.Files.walk(path, 1)) {
            for (final Iterator<Path> iterator = walk.iterator(); iterator.hasNext();) {
                final Path next = iterator.next();
                if (next == path) {
                    continue;
                }
                final File nextTarget = new File(target, next.getName(next.getNameCount() - 1).toString());
                if (PathUtils.isDirectory(next)) {
                    createDirectoryPath(next, nextTarget);
                    continue;
                }
                createFilePath(next, nextTarget);
            }
        }
        return target.toPath();
    }

    private Path createFilePath(final Path path, final File target) throws Exception {
        if (target.exists()) {
            return target.toPath();
        }
        Files.createFile(target);
        try (InputStream input = path.getFileSystem().provider().newInputStream(path, StandardOpenOption.READ)) {
            try (FileOutputStream output = new FileOutputStream(target)) {
                IOUtils.copy(input, output);
            }
        }
        return target.toPath();
    }

    public static Path getExternalPathFor(final String path) {
        return Singleton.get(Resources.class).getExternalPathForImpl(path);
    }

}
