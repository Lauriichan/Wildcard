package me.lauriichan.minecraft.wildcard.core.command.api;

@FunctionalInterface
public interface Command<S> {

    int execute(CommandContext<S> context);

}
