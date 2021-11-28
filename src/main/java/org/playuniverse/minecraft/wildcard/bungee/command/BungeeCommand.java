package org.playuniverse.minecraft.wildcard.bungee.command;

import java.util.Collections;
import java.util.List;

import org.playuniverse.minecraft.wildcard.core.WildcardCore;
import org.playuniverse.minecraft.wildcard.core.command.api.base.BaseCommand;
import org.playuniverse.minecraft.wildcard.core.command.api.redirect.AbstractRedirect;

import net.md_5.bungee.api.CommandSender;

public final class BungeeCommand extends BaseCommand<BungeeInfo> {

    private final String[] aliases;

    public BungeeCommand(final WildcardCore core, final AbstractRedirect<BungeeInfo> redirect, final String name, final String... aliases) {
        super(core, redirect, name);
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    public List<String> onTabComplete(final CommandSender sender, final String[] args) {
        final List<String> list = redirectComplete(new BungeeInfo(core, sender), args);
        if (list == null) {
            return Collections.emptyList();
        }
        if (list.isEmpty() || args.length == 0) {
            return list;
        }
        return list.isEmpty() ? list : matchComplete(list, args[0]);
    }

    public void onCommand(final CommandSender sender, final String[] args) {
        core.getPlugin().getExecutor().execute(() -> {
            final BungeeInfo info = new BungeeInfo(core, sender);
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
    }

}