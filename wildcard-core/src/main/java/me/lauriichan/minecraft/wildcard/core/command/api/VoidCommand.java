package me.lauriichan.minecraft.wildcard.core.command.api;

import java.util.function.Consumer;

@FunctionalInterface
public interface VoidCommand<S> extends Command<S> {

    @Override
    default int execute(final CommandContext<S> context) {
        run(context);
        return 1;
    }

    void run(CommandContext<S> context);

    static <S> VoidCommand<S> of(final Consumer<CommandContext<S>> consumer) {
        return context -> consumer.accept(context);
    }

}
