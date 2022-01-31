package org.playuniverse.minecraft.wildcard.core.command.api.redirect;

import org.playuniverse.minecraft.wildcard.core.command.api.nodes.Node;

public class NodeRedirect<S> extends AbstractRedirect<S> {

    private final Node<S> node;

    public NodeRedirect(final Node<S> node) {
        super(0);
        this.node = node;
    }

    @Override
    public boolean isValid() {
        return node != null;
    }

    @Override
    public Node<S> handleComplete(final String command) {
        return node;
    }

    @Override
    public Node<S> handleCommand(final String command) {
        return node;
    }

    @Override
    public boolean hasGlobal() {
        return true;
    }

    @Override
    public String getGlobal() {
        return node.getName();
    }

}
