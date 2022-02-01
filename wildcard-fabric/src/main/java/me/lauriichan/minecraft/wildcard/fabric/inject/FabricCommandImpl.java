package me.lauriichan.minecraft.wildcard.fabric.inject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import me.lauriichan.minecraft.wildcard.core.util.ReflectHelper;
import me.lauriichan.minecraft.wildcard.fabric.command.FabricCommand;
import net.minecraft.server.command.ServerCommandSource;

final class FabricCommandImpl {

    private FabricCommandImpl() {
        throw new UnsupportedOperationException("constant class");
    }

    @SuppressWarnings("unchecked")
    public static LiteralArgumentBuilder<ServerCommandSource> implementationFor(FabricCommand command, String id) {
        Optional<Method> optional = ReflectHelper.getMethod(FabricCommandImpl.class, id, FabricCommand.class);
        if (!optional.isPresent()) {
            return null;
        }
        try {
            return (LiteralArgumentBuilder<ServerCommandSource>) optional.get().invoke(null, command);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return null;
        }
    }

    private static int execute(FabricCommand command, CommandContext<ServerCommandSource> context) {
        command.execute(context.getSource(), context.getInput().startsWith("/") ? context.getInput().substring(1) : context.getInput());
        return 0;
    }

    private static CompletableFuture<Suggestions> suggest(final FabricCommand command, final CommandContext<ServerCommandSource> context,
        final SuggestionsBuilder builder) throws CommandSyntaxException {
        List<String> list = command.complete(context.getSource(), context.getInput());
        for (int i = 0; i < list.size(); i++) {
            builder.suggest(list.get(i));
        }
        return builder.buildFuture();
    }

    public static LiteralArgumentBuilder<ServerCommandSource> wildcard(FabricCommand command) {
        Command<ServerCommandSource> func = context -> execute(command, context);
        SuggestionProvider<ServerCommandSource> sugs = (context, builder) -> suggest(command, context, builder);
        return LiteralArgumentBuilder.<ServerCommandSource>literal("wildcard").executes(func).then(
            RequiredArgumentBuilder.<ServerCommandSource, String>argument("0", StringArgumentType.greedyString()).suggests(sugs).executes(func));
    }

}
