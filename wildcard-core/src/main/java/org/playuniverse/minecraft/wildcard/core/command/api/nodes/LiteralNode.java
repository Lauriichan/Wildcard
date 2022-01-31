package org.playuniverse.minecraft.wildcard.core.command.api.nodes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.playuniverse.minecraft.wildcard.core.command.api.CommandContext;
import org.playuniverse.minecraft.wildcard.core.command.api.StringReader;

public class LiteralNode<S> extends RootNode<S> {

    protected final LinkedHashMap<String, Node<S>> children = new LinkedHashMap<>();
    protected String execution;

    public LiteralNode(final String name) {
        super(name);
    }

    public Node<S> getChild(final String name) {
        return children.get(name);
    }

    public boolean hasChild(final String name) {
        return children.containsKey(name);
    }

    public boolean putChild(final Node<S> node) {
        if (children.containsKey(node.getName())) {
            return false;
        }
        children.put(node.getName(), node);
        return true;
    }

    public void setExecution(final String execution) {
        this.execution = execution;
    }

    @Override
    public int execute(final CommandContext<S> context) {
        final StringReader reader = context.getReader();
        final int cursor = reader.getCursor();
        String name = reader.skipWhitespace().readUnquoted();
        if (!hasChild(name)) {
            if ((!name.isEmpty() || !hasChild(name = execution))) {
                return 0;
            }
            reader.setCursor(cursor);
        }
        return getChild(name).execute(context);
    }

    @Override
    public List<String> complete(final CommandContext<S> context) {
        final String name = context.getReader().skipWhitespace().readUnquoted();
        if (!hasChild(name)) {
            return new ArrayList<>(children.keySet());
        }
        return getChild(name).complete(context);
    }

}
