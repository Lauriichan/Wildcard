package me.lauriichan.minecraft.wildcard.core.command.api.nodes;

import java.util.List;

import me.lauriichan.minecraft.wildcard.core.command.api.CommandContext;

public class ForkNode<S> extends RootNode<S> {

    private final RootNode<S> fork;

    public ForkNode(final String name, final RootNode<S> fork) {
        super(name);
        this.fork = fork;
    }

    public RootNode<S> getFork() {
        return fork;
    }

    @Override
    public int execute(final CommandContext<S> context) {
        return fork.execute(context);
    }

    @Override
    public List<String> complete(final CommandContext<S> context) {
        return fork.complete(context);
    }

}
