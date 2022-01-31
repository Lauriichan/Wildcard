package org.playuniverse.minecraft.wildcard.core.web.command.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.playuniverse.minecraft.wildcard.core.WildcardCore;
import org.playuniverse.minecraft.wildcard.core.command.Command;
import org.playuniverse.minecraft.wildcard.core.command.api.CommandManager;
import org.playuniverse.minecraft.wildcard.core.command.api.nodes.RootNode;
import org.playuniverse.minecraft.wildcard.core.util.InstanceCreator;
import org.playuniverse.minecraft.wildcard.core.util.ReflectHelper;

import com.syntaxphoenix.syntaxapi.event.Event;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;

public final class CommandRegisterEvent extends Event {

    static final Predicate<String> COMMAND_NAME = Pattern.compile("[/\\da-z_]+").asMatchPredicate();

    private final HashSet<Class<? extends ICommand>> commands = new HashSet<>();

    public void add(final Class<? extends ICommand> command) {
        commands.add(command);
    }

    public void clear() {
        commands.clear();
    }

    public Set<Class<? extends ICommand>> getCommands() {
        return commands;
    }

    int register(final WildcardCore core, final CommandManager<WebSource> manager) {
        int registered = 0;
        final ILogger logger = core.getLogger();
        final ArrayList<String> aliases = new ArrayList<>();
        for (final Class<? extends ICommand> clazz : commands) {
            ICommand command;
            try {
                command = InstanceCreator.create(clazz, core);
            } catch (final Exception e) {
                logger.log(LogTypeId.WARNING, "Failed to create Instance of '" + clazz.getSimpleName() + "'!");
                logger.log(LogTypeId.WARNING, e);
                continue;
            }
            final Optional<Command> infoOption = ReflectHelper.getAnnotationOfMethod(Command.class, clazz, "build", String.class);
            if (infoOption.isEmpty()) {
                logger.log(LogTypeId.WARNING, "Can't find Command annotation for '" + clazz.getSimpleName() + "'!");
                continue;
            }
            final Command info = infoOption.get();
            if (!COMMAND_NAME.test(info.name())) {
                logger.log(LogTypeId.WARNING, "Command '" + clazz.getSimpleName() + "' got an invalid command name (" + info.name() + ")");
                continue;
            }
            final RootNode<WebSource> node = command.build(info.name());
            if (node == null) {
                logger.log(LogTypeId.WARNING, "Command '" + clazz.getSimpleName() + "' returned a null root node");
                continue;
            }
            for (final String alias : info.aliases()) {
                if (!COMMAND_NAME.test(alias)) {
                    logger.log(LogTypeId.WARNING,
                        "Alias '" + alias + "' of command '" + clazz.getSimpleName() + "' is invalid and will be ignored");
                    continue;
                }
                aliases.add(alias);
            }
            manager.register(node, aliases.toArray(String[]::new));
            aliases.clear();
            registered++;
        }
        return registered;
    }

}
