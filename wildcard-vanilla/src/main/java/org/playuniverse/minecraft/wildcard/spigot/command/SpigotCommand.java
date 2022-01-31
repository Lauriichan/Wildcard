package org.playuniverse.minecraft.wildcard.spigot.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.playuniverse.minecraft.wildcard.core.WildcardCore;
import org.playuniverse.minecraft.wildcard.core.command.api.base.BaseCommand;
import org.playuniverse.minecraft.wildcard.core.command.api.redirect.AbstractRedirect;

public final class SpigotCommand extends BaseCommand<SpigotInfo> implements CommandExecutor, TabCompleter {

    private final String[] aliases;
    private final String fallbackPrefix;

    public SpigotCommand(final WildcardCore core, final AbstractRedirect<SpigotInfo> redirect, final String name, final String... aliases) {
        this(core, redirect, core.getPlugin().getId(), name, aliases);
    }

    public SpigotCommand(final WildcardCore core, final AbstractRedirect<SpigotInfo> redirect, final String fallbackPrefix,
        final String name, final String... aliases) {
        super(core, redirect, name);
        this.fallbackPrefix = fallbackPrefix;
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getFallbackPrefix() {
        return fallbackPrefix;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        final List<String> list = redirectComplete(new SpigotInfo(core, sender), args);
        if (list == null) {
            return Collections.emptyList();
        }
        if (list.isEmpty() || args.length == 0) {
            return list;
        }
        return list.isEmpty() ? list : matchComplete(list, args[0]);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        core.getPlugin().getExecutor().execute(() -> {
            final SpigotInfo info = new SpigotInfo(core, sender);
            final int state = redirectCommand(info, args);
            switch (state) {
            case -2:
                info.send("command.execution.failed", "command", getCommandName(args));
                break;
            case -1:
                info.send("command.execution.notfound", "command", getCommandName(args));
                break;
            }
        });
        return false;
    }

}