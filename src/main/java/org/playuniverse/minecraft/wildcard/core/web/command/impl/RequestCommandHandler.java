package org.playuniverse.minecraft.wildcard.core.web.command.impl;

import java.io.File;
import java.util.HashMap;

import org.playuniverse.minecraft.wildcard.core.WildcardCore;
import org.playuniverse.minecraft.wildcard.core.command.api.CommandContext;
import org.playuniverse.minecraft.wildcard.core.command.api.CommandManager;
import org.playuniverse.minecraft.wildcard.core.command.api.nodes.RootNode;
import org.playuniverse.minecraft.wildcard.core.web.WebSender;

import com.syntaxphoenix.syntaxapi.json.JsonArray;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.net.http.HttpWriter;
import com.syntaxphoenix.syntaxapi.net.http.JsonAnswer;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;
import com.syntaxphoenix.syntaxapi.net.http.ResponseCode;
import com.syntaxphoenix.syntaxapi.net.http.StandardContentType;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

public final class RequestCommandHandler {

    private final CommandManager<WebSource> commandManager = new CommandManager<>();
    private final WildcardCore core;

    public RequestCommandHandler(final WildcardCore core) {
        this.core = core;
        final CommandRegisterEvent event = new CommandRegisterEvent();
        core.getEventManager().call(event);
        core.getLogger().log(LogTypeId.INFO,
            "Registered " + event.register(core, commandManager) + " of " + event.getCommands().size() + " Web commands!");
    }

    public void call(final File directory, final WebSender sender, final HttpWriter writer, final ReceivedRequest request,
        final String string) throws Exception {
        final HashMap<String, String> parsed = parse(string);
        String command = readCommand(parsed, request.getPathAsString(), request.getPath());
        if (command.isBlank()) {
            final RootNode<?> node = commandManager.getGlobal();
            if (node == null) {
                new JsonAnswer(StandardContentType.JSON).respond("message", "Command isn't set!").code(ResponseCode.NOT_ACCEPTABLE)
                    .write(writer);
                return;
            }
            command = node.getName();
        }
        if (!commandManager.hasCommand(command)) {
            new JsonAnswer(StandardContentType.JSON).respond("message", "Command doesn't exist!").code(ResponseCode.NOT_FOUND)
                .write(writer);
            return;
        }
        final RootNode<WebSource> node = commandManager.getCommand(command);
        if (node == null) {
            new JsonAnswer(StandardContentType.JSON).respond("message", "Command doesn't exist!").code(ResponseCode.NOT_FOUND)
                .write(writer);
            return;
        }
        try {
            final int state = node.execute(new CommandContext<>(new WebSource(core, directory, sender, writer, request), asString(parsed)));
            switch (state) {
            case 1:
                new JsonAnswer(StandardContentType.JSON).respond("message", "Successfully executed command!").code(ResponseCode.OK).write(writer);
                return;
            case -1:
                new JsonAnswer(StandardContentType.JSON).respond("message", "Failed to execute command!")
                    .code(ResponseCode.INTERNAL_SERVER_ERROR).write(writer);
            }
        } catch (final Throwable exp) {
            final JsonArray array = new JsonArray();
            for (final String line : Exceptions.stackTraceToStringArray(exp)) {
                array.add(line);
            }
            new JsonAnswer(StandardContentType.JSON).respond("message", "Failed to execute command!").respond("error", array)
                .code(ResponseCode.INTERNAL_SERVER_ERROR).write(writer);
            return;
        }
    }

    private String readCommand(final HashMap<String, String> parsed, final String pathString, final String[] path) {
        if (parsed.containsKey("_command")) {
            final String command = parsed.get("_command");
            if (!command.isBlank()) {
                return command;
            }
        }
        final String command = (pathString.endsWith("/") ? "" : "/") + (path.length == 0 ? "index" : path[path.length - 1]);
        return command.isBlank() ? pathString + "index" : pathString;
    }

    private String asString(final HashMap<String, String> input) {
        final StringBuilder builder = new StringBuilder();
        for (final String key : input.keySet()) {
            if (key.startsWith("_")) {
                continue;
            }
            builder.append(key).append(' ');
        }
        return builder.substring(0, builder.length() - 1);
    }

    private HashMap<String, String> parse(String input) {
        final HashMap<String, String> map = new HashMap<>();
        final String[] entries = (input = input.replaceFirst("\\?", "")).contains("&") ? input.split("&")
            : new String[] {
                input
            };
        for (int index = 0; index < entries.length; index++) {
            final String current = entries[index];
            if (!current.contains("=")) {
                map.put(current, null);
                continue;
            }
            final String[] entry = current.split("=", 2);
            map.put(entry[0].toLowerCase(), entry[1].toLowerCase());
        }
        return map;
    }

}
