package me.lauriichan.minecraft.wildcard.core.web.command.impl;

import java.io.File;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.net.http.HttpWriter;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.web.WebSender;

public class WebSource {

    private final WildcardCore core;

    private final File directory;
    private final WebSender sender;
    private final HttpWriter writer;
    private final ReceivedRequest request;

    public WebSource(final WildcardCore core, final File directory, final WebSender sender, final HttpWriter writer,
        final ReceivedRequest request) {
        this.core = core;
        this.sender = sender;
        this.writer = writer;
        this.request = request;
        this.directory = directory;
    }

    public final String getHost() {
        return core.getWebControl().getHostPath();
    }

    public final ILogger getLogger() {
        return core.getLogger();
    }

    public final WildcardCore getCore() {
        return core;
    }

    public final File getDirectory() {
        return directory;
    }

    public final WebSender getSender() {
        return sender;
    }

    public final HttpWriter getWriter() {
        return writer;
    }

    public final ReceivedRequest getRequest() {
        return request;
    }

}
