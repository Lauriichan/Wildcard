package me.lauriichan.minecraft.wildcard.core.command.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.syntaxphoenix.syntaxapi.utils.java.Arrays;

import me.lauriichan.minecraft.wildcard.core.command.api.nodes.ForkNode;
import me.lauriichan.minecraft.wildcard.core.command.api.nodes.RootNode;

public class CommandManager<S> {

    private final ConcurrentHashMap<String, RootNode<S>> commands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ArrayList<String>> aliases = new ConcurrentHashMap<>();

    private String global = null;

    public HashMap<RootNode<S>, String[]> getCommands() {
        final HashMap<RootNode<S>, String[]> map = new HashMap<>();
        for (final RootNode<S> node : commands.values()) {
            fillCommand(node, map);
        }
        return map;
    }

    private void fillCommand(final RootNode<S> node, final HashMap<RootNode<S>, String[]> map) {
        if (node instanceof ForkNode) {
            fillForkNode((ForkNode<S>) node, map);
            return;
        }
        fillNode(node, map);
    }

    private void fillForkNode(final ForkNode<S> node, final HashMap<RootNode<S>, String[]> map) {
        final RootNode<S> root = findNonFork(node);
        if (!map.containsKey(root)) {
            map.put(root, new String[] {
                node.getName()
            });
            return;
        }
        final String[] aliases = map.get(root);
        Arrays.merge(String[]::new, aliases, node.getName());
        map.put(root, aliases);
    }

    private RootNode<S> findNonFork(final ForkNode<S> node) {
        if (node.getFork() instanceof ForkNode) {
            return findNonFork((ForkNode<S>) node.getFork());
        }
        return node.getFork();
    }

    private void fillNode(final RootNode<S> node, final HashMap<RootNode<S>, String[]> map) {
        if (map.containsKey(node)) {
            final String[] aliases = map.get(node);
            Arrays.merge(String[]::new, aliases, node.getName());
            map.put(node, aliases);
            return;
        }
        map.put(node, new String[] {
            node.getName()
        });
    }

    public CommandState register(final RootNode<S> node, final String[] aliases) {
        if (commands.containsKey(node.getName())) {
            return CommandState.FAILED;
        }
        commands.put(node.getName(), node);
        final ArrayList<String> conflicts = new ArrayList<>();
        for (final String alias : aliases) {
            if (!commands.containsKey(alias)) {
                commands.put(alias, new ForkNode<>(alias, node));
                continue;
            }
            conflicts.add(alias);
        }
        return conflicts.isEmpty() ? CommandState.SUCCESS : CommandState.PARTIAL.setAliases(conflicts.toArray(String[]::new));
    }

    public boolean unregisterCommand(final String name) {
        if (!commands.containsKey(name)) {
            return false;
        }
        final Optional<Entry<String, ArrayList<String>>> optional = aliases.entrySet().stream()
            .filter(entry -> entry.getValue().contains(name)).findFirst();
        if (optional.isPresent()) {
            final Entry<String, ArrayList<String>> entry = optional.get();
            entry.getValue().remove(name);
            if (entry.getValue().isEmpty()) {
                aliases.remove(entry.getKey());
            }
        }
        commands.remove(name);
        return true;
    }

    public CommandManager<S> setGlobal(final String global) {
        this.global = global;
        return this;
    }

    public boolean hasGlobal() {
        return global != null && getCommand(global) != null;
    }

    public RootNode<S> getGlobal() {
        return global != null ? getCommand(global) : null;
    }

    public RootNode<S> getCommandOrGlobal(final String name) {
        final RootNode<S> node = getCommand(name);
        if (node == null && hasGlobal()) {
            return getGlobal();
        }
        return node;
    }

    public RootNode<S> getCommand(final String name) {
        return commands.get(name);
    }

    public boolean hasCommand(final String name) {
        return commands.containsKey(name);
    }

}
