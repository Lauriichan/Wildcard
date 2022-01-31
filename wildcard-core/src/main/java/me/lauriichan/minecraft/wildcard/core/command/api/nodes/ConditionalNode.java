package me.lauriichan.minecraft.wildcard.core.command.api.nodes;

import java.util.List;
import java.util.function.Predicate;

import me.lauriichan.minecraft.wildcard.core.command.api.CommandContext;

public class ConditionalNode<S> extends RootNode<S> {

    private final Predicate<CommandContext<S>> predicate;
    private final Node<S> node;

    public ConditionalNode(final Node<S> node, final Predicate<CommandContext<S>> predicate) {
        super(node.getName());
        this.node = node;
        this.predicate = predicate;
    }

    @Override
    public int execute(final CommandContext<S> context) {
        if (!predicate.test(context)) {
            return -1;
        }
        return node.execute(context);
    }

    @Override
    public List<String> complete(final CommandContext<S> context) {
        if (!predicate.test(context)) {
            return null;
        }
        return node.complete(context);
    }

}