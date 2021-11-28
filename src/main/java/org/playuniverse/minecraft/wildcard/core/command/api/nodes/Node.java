package org.playuniverse.minecraft.wildcard.core.command.api.nodes;

import java.util.List;

import org.playuniverse.minecraft.wildcard.core.command.api.CommandContext;

public abstract class Node<S> {

    protected final String name;

    public Node(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract int execute(CommandContext<S> context);

    public List<String> complete(final CommandContext<S> context) {
        return null;
    }

}
