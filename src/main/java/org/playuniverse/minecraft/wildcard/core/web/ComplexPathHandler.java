package org.playuniverse.minecraft.wildcard.core.web;

import java.io.File;
import java.util.Optional;

import org.playuniverse.minecraft.wildcard.core.util.Awaiter;
import org.playuniverse.minecraft.wildcard.core.web.util.PathRequestEvent;
import org.playuniverse.minecraft.wildcard.core.web.util.PlaceholderFileAnswer;

import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.net.http.HttpWriter;
import com.syntaxphoenix.syntaxapi.net.http.NamedAnswer;
import com.syntaxphoenix.syntaxapi.net.http.NamedType;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;
import com.syntaxphoenix.syntaxapi.net.http.ResponseCode;
import com.syntaxphoenix.syntaxapi.net.http.StandardNamedType;

public final class ComplexPathHandler implements ISpecialPathHandler {

    private final EventManager manager;

    public ComplexPathHandler(final EventManager manager) {
        this.manager = manager;
    }

    @Override
    public void handleWeb(final File directory, final WebSender sender, final HttpWriter writer, final ReceivedRequest data)
        throws Exception {
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
            final PathRequestEvent event = new PathRequestEvent(file, StandardNamedType.HTML, sender, data);
            Awaiter.of(manager.call(event)).await();
            if (event.isCancelled()) {
                new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.FORBIDDEN).write(writer);
                return;
            }
            new PlaceholderFileAnswer(file, StandardNamedType.HTML, sender, data, manager).code(ResponseCode.OK).write(writer);
            return;
        }

        final String type0 = data.getPath()[data.getPath().length - 1];
        final String[] type1 = type0.contains(".") ? type0.split("\\.")
            : new String[] {
                ""
            };
        final NamedType type = Optional.ofNullable(StandardNamedType.parse(type1[type1.length - 1])).map(value -> (NamedType) value)
            .orElseGet(() -> FileNamedType.parse(type1[type1.length - 1]));
        if (type == null) {
            new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.NOT_FOUND).write(writer);
            return;
        }
        final PathRequestEvent event = new PathRequestEvent(file, type, sender, data);
        Awaiter.of(manager.call(event)).await();
        if (event.isCancelled()) {
            new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.FORBIDDEN).write(writer);
            return;
        }
        new PlaceholderFileAnswer(file, type, sender, data, manager).code(ResponseCode.OK).write(writer);
    }

}
