package me.lauriichan.minecraft.wildcard.core.command.api.nodes;

import java.util.List;

import me.lauriichan.minecraft.wildcard.core.command.api.CommandContext;

public class RedirectNode<S> extends SubNode<S> {

    private final SubNode<S> redirect;

    public RedirectNode(final String name, final SubNode<S> redirect) {
        super(name);
        this.redirect = redirect;
    }

    public SubNode<S> getRedirect() {
        return redirect;
    }

    @Override
    public int execute(final CommandContext<S> context) {
        return redirect.execute(context);
    }

    @Override
    public List<String> complete(final CommandContext<S> context) {
        return redirect.complete(context);
    }

}
