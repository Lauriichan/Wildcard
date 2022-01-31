package me.lauriichan.minecraft.wildcard.core.command.api.nodes;

import java.util.List;

import me.lauriichan.minecraft.wildcard.core.command.api.Command;
import me.lauriichan.minecraft.wildcard.core.command.api.CommandContext;
import me.lauriichan.minecraft.wildcard.core.command.api.Complete;
import me.lauriichan.minecraft.wildcard.core.command.api.VoidCommand;

public class CommandNode<S> extends RootNode<S> {

    private final Command<S> command;
    private final Complete<S> complete;

    public CommandNode(final String name, final Command<S> command) {
        super(name);
        this.command = command;
        this.complete = Complete.nothing();
    }

    public CommandNode(final String name, final VoidCommand<S> command) {
        super(name);
        this.command = command;
        this.complete = Complete.nothing();
    }

    public CommandNode(final String name, final Command<S> command, final Complete<S> complete) {
        super(name);
        this.command = command;
        this.complete = complete;
    }

    public CommandNode(final String name, final VoidCommand<S> command, final Complete<S> complete) {
        super(name);
        this.command = command;
        this.complete = complete;
    }

    public Command<S> getCommand() {
        return command;
    }

    public Complete<S> getComplete() {
        return complete;
    }

    @Override
    public int execute(final CommandContext<S> context) {
        return command.execute(context);
    }

    @Override
    public List<String> complete(final CommandContext<S> context) {
        return complete.complete(context);
    }

}