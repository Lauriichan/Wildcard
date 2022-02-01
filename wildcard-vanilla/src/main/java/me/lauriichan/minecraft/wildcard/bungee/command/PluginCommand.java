package me.lauriichan.minecraft.wildcard.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public final class PluginCommand extends Command implements TabExecutor {

    private final BungeeCommand command;

    public PluginCommand(final BungeeCommand command) {
        super(command.getName(), null, command.getAliases());
        this.command = command;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        command.onCommand(sender, args);
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        return command.onTabComplete(sender, args);
    }

}
