package me.lauriichan.minecraft.wildcard.core.command.api.redirect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import me.lauriichan.minecraft.wildcard.core.command.api.CommandManager;
import me.lauriichan.minecraft.wildcard.core.command.api.nodes.RootNode;

public class ManagerRedirect<S> extends AbstractRedirect<S> {

    private final CommandManager<S> manager;

    private Function<String, Boolean> condition;

    public ManagerRedirect(final CommandManager<S> manager) {
        super(1);
        this.manager = manager;
    }

    @Override
    public boolean isValid() {
        return manager != null;
    }

    public void setCondition(final Function<String, Boolean> condition) {
        this.condition = condition;
    }

    @Override
    public RootNode<S> handleComplete(final String command) {
        return handleCommand(command);
    }

    @Override
    public List<String> handleNullComplete(final S source, final String[] args) {
        return args.length <= 1 ? collectCommands() : null;
    }

    @Override
    public RootNode<S> handleCommand(final String command) {
        if (command == null) {
            if (condition != null && !condition.apply(command)) {
                return null;
            }
            return manager.getGlobal();
        }
        final RootNode<S> node = manager.getCommand(command);
        if (node == null && hasGlobal()) {
            if (condition != null && !condition.apply(command)) {
                return null;
            }
            return manager.getGlobal();
        }
        return node;
    }

    @Override
    public boolean hasGlobal() {
        return manager.hasGlobal();
    }

    @Override
    public String getGlobal() {
        return manager.hasGlobal() ? manager.getGlobal().getName() : null;
    }

    private List<String> collectCommands() {
        if (manager.getCommands().isEmpty()) {
            return null;
        }
        final ArrayList<String> commands = new ArrayList<>();
        manager.getCommands().values().forEach(array -> Collections.addAll(commands, array));
        return commands;
    }

}
