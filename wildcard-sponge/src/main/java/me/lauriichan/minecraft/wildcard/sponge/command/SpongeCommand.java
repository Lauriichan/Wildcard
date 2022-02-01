package me.lauriichan.minecraft.wildcard.sponge.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseCommand;
import me.lauriichan.minecraft.wildcard.core.command.api.redirect.AbstractRedirect;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import net.kyori.adventure.text.Component;

public final class SpongeCommand extends BaseCommand<SpongeInfo> implements Command {

    private final String[] aliases;
    private final String fallbackPrefix;

    public SpongeCommand(final WildcardCore core, final AbstractRedirect<SpongeInfo> redirect, final String name, final String... aliases) {
        this(core, redirect, core.getPlugin().getId(), name, aliases);
    }

    public SpongeCommand(final WildcardCore core, final AbstractRedirect<SpongeInfo> redirect, final String fallbackPrefix,
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
    public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
        final SpongeInfo info = new SpongeInfo(core, cause);
        final String input = arguments.input();
        final int state = redirectCommand(info, arguments.input()) + 1;
        PlatformComponent[] error = null;
        switch (state) {
        case -1:
            error = info.translate("command.execution.failed", "command", getCommandName(input));
            break;
        case 0:
            error = info.translate("command.execution.notfound", "command", getCommandName(input));
            break;
        }
        if (error != null) {
            return CommandResult.builder().result(state).error((Component) core.getComponentParser().getAdapter().asHandle(error)[0])
                .build();
        }
        return CommandResult.builder().result(state).build();
    }

    @Override
    public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
        ArrayList<CommandCompletion> list = new ArrayList<>();
        final String input = arguments.input();
        List<String> completion = redirectComplete(new SpongeInfo(core, cause), input);
        if (completion != null && !completion.isEmpty()) {
            for (int i = 0; i < completion.size(); i++) {
                list.add(CommandCompletion.of(completion.get(i))); // Possibly match to last argument in future
            }
        }
        return list;
    }

    @Override
    public boolean canExecute(CommandCause cause) {
        return true;
    }

    @Override
    public Optional<Component> shortDescription(CommandCause cause) {
        return Optional.empty();
    }

    @Override
    public Optional<Component> extendedDescription(CommandCause cause) {
        return Optional.empty();
    }

    @Override
    public Component usage(CommandCause cause) {
        return Component.text("/" + name);
    }

}