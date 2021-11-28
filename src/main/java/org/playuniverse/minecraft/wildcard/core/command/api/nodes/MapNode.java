package org.playuniverse.minecraft.wildcard.core.command.api.nodes;

import java.util.List;
import java.util.function.Function;

import org.playuniverse.minecraft.wildcard.core.command.api.CommandContext;

public class MapNode<OS, NS> extends RootNode<OS> {

    private final Function<OS, NS> function;
    private final Node<NS> node;

    public MapNode(final Function<OS, NS> function, final Node<NS> node) {
        super(node.getName());
        this.function = function;
        this.node = node;
    }

    @Override
    public int execute(final CommandContext<OS> context) {
        return node.execute(new CommandContext<>(function.apply(context.getSource()), context.getReader()));
    }

    @Override
    public List<String> complete(final CommandContext<OS> context) {
        return node.complete(new CommandContext<>(function.apply(context.getSource()), context.getReader()));
    }

}
