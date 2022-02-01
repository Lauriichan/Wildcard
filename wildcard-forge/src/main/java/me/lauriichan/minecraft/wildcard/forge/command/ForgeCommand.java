package me.lauriichan.minecraft.wildcard.forge.command;

import java.util.Collections;
import java.util.List;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseCommand;
import me.lauriichan.minecraft.wildcard.core.command.api.redirect.AbstractRedirect;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;

public final class ForgeCommand extends BaseCommand<ForgeInfo> {

    private final String[] aliases;
    private final String fallbackPrefix;

    public ForgeCommand(final WildcardCore core, final AbstractRedirect<ForgeInfo> redirect, final String name, final String... aliases) {
        this(core, redirect, core.getPlugin().getId(), name, aliases);
    }

    public ForgeCommand(final WildcardCore core, final AbstractRedirect<ForgeInfo> redirect, final String fallbackPrefix, final String name,
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

    public List<String> complete(CommandSource source, String input) {
        List<String> list = redirectComplete(new ForgeInfo(core, source), input);
        if (list == null) {
            return Collections.emptyList();
        }
        if (list.isEmpty() || input.isEmpty()) {
            return list;
        }
        return list;
    }

    public void execute(CommandSource cause, String input) {
        final ForgeInfo info = new ForgeInfo(core, cause);
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
        cause.sendFailure((ITextComponent) core.getComponentParser().getAdapter().asHandle(error)[0]);
    }

}