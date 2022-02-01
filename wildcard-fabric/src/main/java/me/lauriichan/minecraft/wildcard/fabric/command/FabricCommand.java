package me.lauriichan.minecraft.wildcard.fabric.command;

import java.util.Collections;
import java.util.List;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseCommand;
import me.lauriichan.minecraft.wildcard.core.command.api.redirect.AbstractRedirect;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;

public class FabricCommand extends BaseCommand<FabricInfo> {

    private final String[] aliases;
    private final String fallbackPrefix;

    public FabricCommand(final WildcardCore core, final AbstractRedirect<FabricInfo> redirect, final String name, final String... aliases) {
        this(core, redirect, core.getPlugin().getId(), name, aliases);
    }

    public FabricCommand(final WildcardCore core, final AbstractRedirect<FabricInfo> redirect, final String fallbackPrefix, final String name,
        final String... aliases) {
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

    public List<String> complete(ServerCommandSource source, String input) {
        List<String> list = redirectComplete(new FabricInfo(core, source), input);
        if (list == null) {
            return Collections.emptyList();
        }
        if (list.isEmpty() || input.isEmpty()) {
            return list;
        }
        return list;
    }

    public void execute(ServerCommandSource cause, String input) {
        final FabricInfo info = new FabricInfo(core, cause);
        final int state = redirectCommand(info, input);
        PlatformComponent[] error = null;
        switch (state) {
        case -2:
            error = info.translate("command.execution.failed", "command", getCommandName(input));
            break;
        case -1:
            error = info.translate("command.execution.notfound", "command", getCommandName(input));
            break;
        }
        if (error == null) {
            return;
        }
        cause.sendError((MutableText) core.getComponentParser().getAdapter().asHandle(error)[0]);
    }

}