package org.playuniverse.minecraft.wildcard.core.command.api;

import java.util.List;

@FunctionalInterface
public interface Complete<S> {

    Complete<?> DEFAULT = ignore -> null;

    @SuppressWarnings("unchecked")
    static <E> Complete<E> nothing() {
        return (Complete<E>) DEFAULT;
    }

    List<String> complete(CommandContext<S> context);

}
