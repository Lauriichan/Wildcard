package me.lauriichan.minecraft.wildcard.sponge.command;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseCommand;
import me.lauriichan.minecraft.wildcard.core.command.api.redirect.AbstractRedirect;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;

public final class SpongeCommand extends BaseCommand<SpongeInfo> implements CommandCallable {

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
    public CommandResult process(CommandSource source, String input) throws CommandException {
        final String[] args = input.split(" ");
        final SpongeInfo info = new SpongeInfo(core, source);
        final int state = redirectCommand(info, args) + 1;
        PlatformComponent[] error = null;
        switch (state) {
        case -1:
            error = info.translate("command.execution.failed", "command", getCommandName(args));
            break;
        case 0:
            error = info.translate("command.execution.notfound", "command", getCommandName(args));
            break;
        }
        if (error != null) {
            source.sendMessage((Text) core.getComponentParser().getAdapter().asHandle(error)[0]);
            return CommandResult.builder().successCount(state).build();
        }
        return CommandResult.builder().successCount(state).build();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String input, Location<World> targetPosition) throws CommandException {
        List<String> completion = redirectComplete(new SpongeInfo(core, source), input.split(" "));
        if (completion != null && !completion.isEmpty()) {
            return completion;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return LiteralText.of("/" + name);
    }

}