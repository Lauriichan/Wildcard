package org.playuniverse.minecraft.wildcard.core.web;

import org.playuniverse.minecraft.wildcard.core.web.session.ClientSession;

import com.syntaxphoenix.syntaxapi.net.http.HttpSender;

public class WebSender extends HttpSender {

    private final HttpSender sender;
    private final ClientSession session;

    public WebSender(final HttpSender sender, final ClientSession session) {
        super(sender.getClient(), sender.getInput());
        this.sender = sender;
        this.session = session;
    }

    public ClientSession getSession() {
        return session;
    }

    public boolean hasSession() {
        return session != null;
    }

    public HttpSender getSender() {
        return sender;
    }

}
