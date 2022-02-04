package me.lauriichan.minecraft.wildcard.core.command.api.base;

import java.util.List;

import com.syntaxphoenix.syntaxapi.logging.LogTypeId;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.command.api.CommandContext;
import me.lauriichan.minecraft.wildcard.core.command.api.StringReader;
import me.lauriichan.minecraft.wildcard.core.command.api.nodes.Node;
import me.lauriichan.minecraft.wildcard.core.command.api.redirect.AbstractRedirect;
import me.lauriichan.minecraft.wildcard.core.util.registry.IUnique;

public abstract class BaseCommand<S extends BaseInfo> implements IUnique {

    protected final AbstractRedirect<S> redirect;
    protected final WildcardCore core;
    protected final String name;

    public BaseCommand(final WildcardCore core, final AbstractRedirect<S> redirect, final String name) {
        this.redirect = redirect;
        this.core = core;
        this.name = name;
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    public WildcardCore getCore() {
        return core;
    }

    public AbstractRedirect<S> getRedirect() {
        return redirect;
    }

    protected List<String> matchComplete(final List<String> complete, final String argument) {
        if (!argument.trim().isEmpty()) {
            return complete;
        }
        final String lowerArgument = argument.trim().toLowerCase();
        int size = complete.size();
        for (int index = 0; index < size; index++) {
            if (!complete.get(index).toLowerCase().contains(lowerArgument)) {
                continue;
            }
            complete.remove(index--);
            size--;
        }
        return complete;
    }

    protected List<String> redirectComplete(final S info, final String arguments) {
        StringReader reader = new StringReader(arguments).skipWhitespace();
        if (!reader.hasNext() && !redirect.hasGlobal()) {
            return redirect.handleNullComplete(info, "");
        }
        String command = null;
        final Node<S> node = !reader.hasNext() ? redirect.hasGlobal() ? redirect.handleCommand(null) : null
            : redirect.handleCommand(command = reader.read());
        if (node == null) {
            return redirect.handleNullComplete(info, arguments);
        }
        try {
            return node.complete(new CommandContext<>(info, reader.skipWhitespace().getRemaining()));
        } catch (final Throwable throwable) {
            info.getLogger().log(LogTypeId.ERROR, "Failed to complete command '" + command + "'!");
            info.getLogger().log(LogTypeId.ERROR, throwable);
            return null;
        }
    }

    protected List<String> redirectComplete(final S info, final String[] arguments) {
        if (arguments.length == 0 && !redirect.hasGlobal()) {
            return redirect.handleNullComplete(info, "");
        }
        final Node<S> node = arguments.length == 0 ? redirect.handleComplete(null) : redirect.handleComplete(arguments[0]);
        if (node == null) {
            return redirect.handleNullComplete(info, buildArguments(0, arguments));
        }
        try {
            return node.complete(new CommandContext<>(info, buildArguments(arguments)));
        } catch (final Throwable throwable) {
            info.getLogger().log(LogTypeId.ERROR, "Failed to complete command '" + arguments[0] + "'!");
            info.getLogger().log(LogTypeId.ERROR, throwable);
            return null;
        }
    }

    protected int redirectCommand(final S info, final String arguments) {
        StringReader reader = new StringReader(arguments).skipWhitespace();
        String command = null;
        final Node<S> node = !reader.hasNext() ? redirect.hasGlobal() ? redirect.handleCommand(null) : null
            : redirect.handleCommand(command = reader.read());
        if (node == null) {
            return -1;
        }
        try {
            return node.execute(new CommandContext<>(info, reader.skipWhitespace().getRemaining()));
        } catch (final Throwable throwable) {
            info.getLogger().log(LogTypeId.ERROR, "Failed to execute command '" + command + "'!");
            info.getLogger().log(LogTypeId.ERROR, throwable);
            return -2;
        }
    }

    protected int redirectCommand(final S info, final String[] arguments) {
        final Node<S> node = arguments.length == 0 ? redirect.hasGlobal() ? redirect.handleCommand(null) : null
            : redirect.handleCommand(arguments[0]);
        if (node == null) {
            return -1;
        }
        try {
            return node.execute(new CommandContext<>(info, buildArguments(arguments)));
        } catch (final Throwable throwable) {
            info.getLogger().log(LogTypeId.ERROR, "Failed to execute command '" + arguments[0] + "'!");
            info.getLogger().log(LogTypeId.ERROR, throwable);
            return -2;
        }
    }

    protected String getCommandName(final String arguments) {
        StringReader reader = new StringReader(arguments).skipWhitespace();
        if (!reader.hasNext()) {
            return redirect.getGlobal();
        }
        return reader.read();
    }

    protected String getCommandName(final String[] arguments) {
        return arguments.length == 0 ? redirect.getGlobal() : redirect.getArgumentStartIndex() == 0 ? redirect.getGlobal() : arguments[0];
    }

    /*
     * Helper
     */

    protected String buildArguments(final String[] arguments) {
        return buildArguments(redirect.getArgumentStartIndex(), arguments);
    }

    protected String buildArguments(final int start, final String[] arguments) {
        if (arguments.length <= start) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (int index = start; index < arguments.length; index++) {
            builder.append(arguments[index]).append(" ");
        }
        return builder.substring(0, builder.length() - 1);
    }

}
