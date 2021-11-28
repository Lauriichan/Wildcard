package org.playuniverse.minecraft.wildcard.spigot.inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.playuniverse.minecraft.wildcard.core.util.inject.Injector;
import org.playuniverse.minecraft.wildcard.core.util.reflection.ClassLookupProvider;
import org.playuniverse.minecraft.wildcard.core.util.registry.Registry;
import org.playuniverse.minecraft.wildcard.core.util.registry.UniqueRegistry;
import org.playuniverse.minecraft.wildcard.spigot.command.SpigotCommand;

public class SpigotCommands extends Injector<SpigotCommand> {

    private final UniqueRegistry<SpigotCommand> registry = new UniqueRegistry<>();
    private final Registry<SpigotCommand, PluginCommand> commands = new Registry<>();

    @Override
    public Class<SpigotCommand> getType() {
        return SpigotCommand.class;
    }

    @Override
    protected void onSetup(final ClassLookupProvider provider) {
        provider.createLookup("PluginCommand", "org.bukkit.command.PluginCommand").searchConstructor("init", String.class, Plugin.class);
        provider.createCBLookup("CraftCommandMap", "command.CraftCommandMap").searchMethod("sourceMap", "getKnownCommands");
        provider.createCBLookup("CraftServer", "CraftServer").searchMethod("commandMap", "getCommandMap");
    }

    @Override
    protected void inject0(final ClassLookupProvider provider, final SpigotCommand transfer) {
        if (transfer == null || registry.isRegistered(transfer.getId())) {
            return;
        }
        final SimpleCommandMap map = (SimpleCommandMap) provider.getLookup("CraftServer").run(Bukkit.getServer(), "commandMap");
        final PluginCommand command = (PluginCommand) provider.getLookup("PluginCommand").init("init", transfer.getId(),
            transfer.getCore().getPlugin());
        command.setExecutor(transfer);
        command.setTabCompleter(transfer);
        command.setAliases(Arrays.asList(transfer.getAliases()));
        if (!map.register(transfer.getFallbackPrefix(), command)) {
            throw new IllegalStateException("Failed to register command '" + transfer.getFallbackPrefix() + ':' + command.getName() + "'!");
        }
        registry.register(transfer);
        commands.register(transfer, command);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void uninject0(final ClassLookupProvider provider, final SpigotCommand transfer) {
        if (transfer == null || transfer.getId() == null || !registry.isRegistered(transfer.getId())) {
            return;
        }
        final SimpleCommandMap commandMap = (SimpleCommandMap) provider.getLookup("CraftServer").run(Bukkit.getServer(), "commandMap");
        final Map<String, Command> map = (Map<String, Command>) provider.getLookup("CraftCommandMap").run(commandMap, "sourceMap");
        final SpigotCommand command = registry.get(transfer.getId());
        final PluginCommand bukkitCommand = commands.get(command);
        registry.unregister(command.getId());
        commands.unregister(command);
        final ArrayList<String> aliases = new ArrayList<>(bukkitCommand.getAliases());
        aliases.add(command.getName());
        Collections.addAll(aliases, aliases.stream().map(string -> command.getFallbackPrefix() + ':' + string).toArray(String[]::new));
        for (final String alias : aliases) {
            if (map.get(alias) != bukkitCommand) {
                continue;
            }
            map.remove(alias);
        }
        bukkitCommand.unregister(commandMap);
    }

    @Override
    protected void uninjectAll0(final ClassLookupProvider provider) {
        if (registry.isEmpty()) {
            return;
        }
        final SpigotCommand[] array = registry.values().toArray(SpigotCommand[]::new);
        for (final SpigotCommand transfer : array) {
            uninject0(provider, transfer);
        }
    }

    @Override
    protected void dispose() {
        registry.dispose();
        commands.dispose();
    }

}