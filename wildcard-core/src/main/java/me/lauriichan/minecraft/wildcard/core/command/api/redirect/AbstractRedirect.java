package me.lauriichan.minecraft.wildcard.core.command.api.redirect;

import java.util.Collections;
import java.util.List;

import me.lauriichan.minecraft.wildcard.core.command.api.nodes.Node;

public abstract class AbstractRedirect<S> {

    private final int startIndex;

    public AbstractRedirect(final int startIndex) {
        this.startIndex = startIndex;
    }

    public abstract Node<S> handleComplete(String command);

    public List<String> handleNullComplete(final S root, final String args) {
        return Collections.emptyList();
    }

    public abstract Node<S> handleCommand(String command);

    public abstract boolean hasGlobal();

    public abstract String getGlobal();

    public abstract boolean isValid();

    public final int getArgumentStartIndex() {
        return startIndex;
    }

}
