package org.playuniverse.minecraft.wildcard.core.web.util;

import java.io.File;

import org.playuniverse.minecraft.wildcard.core.web.WebSender;

import com.syntaxphoenix.syntaxapi.event.Cancelable;
import com.syntaxphoenix.syntaxapi.event.Event;
import com.syntaxphoenix.syntaxapi.net.http.NamedType;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;

public final class PathRequestEvent extends Event implements Cancelable {

    private final ReceivedRequest request;
    private final WebSender sender;
    private final NamedType type;
    private final File file;

    private boolean cancelled = false;

    public PathRequestEvent(final File file, final NamedType type, final WebSender sender, final ReceivedRequest request) {
        this.file = file;
        this.type = type;
        this.sender = sender;
        this.request = request;
    }

    public File getFile() {
        return file;
    }

    public NamedType getType() {
        return type;
    }

    public WebSender getSender() {
        return sender;
    }

    public ReceivedRequest getRequest() {
        return request;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

}
