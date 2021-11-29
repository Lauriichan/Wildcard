package org.playuniverse.minecraft.wildcard.core.web.command;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.command.Command;
import org.playuniverse.minecraft.wildcard.core.command.api.CommandContext;
import org.playuniverse.minecraft.wildcard.core.command.api.StringReader;
import org.playuniverse.minecraft.wildcard.core.command.api.nodes.CommandNode;
import org.playuniverse.minecraft.wildcard.core.command.api.nodes.RootNode;
import org.playuniverse.minecraft.wildcard.core.data.container.api.IDataType;
import org.playuniverse.minecraft.wildcard.core.data.storage.Database;
import org.playuniverse.minecraft.wildcard.core.data.storage.RequestResult;
import org.playuniverse.minecraft.wildcard.core.util.placeholder.ForkTemplate;
import org.playuniverse.minecraft.wildcard.core.util.placeholder.Template;
import org.playuniverse.minecraft.wildcard.core.web.command.impl.ICommand;
import org.playuniverse.minecraft.wildcard.core.web.command.impl.WebSource;
import org.playuniverse.minecraft.wildcard.core.web.util.PageInjectPlaceholderEvent;
import org.playuniverse.minecraft.wildcard.core.web.util.PlaceholderFileAnswer;

import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.net.http.NamedAnswer;
import com.syntaxphoenix.syntaxapi.net.http.ResponseCode;
import com.syntaxphoenix.syntaxapi.net.http.StandardNamedType;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public class TokenLoginCommand implements ICommand {

    @Override
    @Command(name = "/index")
    public RootNode<WebSource> build(final String name) {
        return new CommandNode<>(name, this::onLogin);
    }

    public int onLogin(final CommandContext<WebSource> context) {
        final StringReader reader = context.getReader();
        final WebSource source = context.getSource();

        final File file = getFile(source.getDirectory(), source.getRequest().getPathAsString());
        if (file == null) {
            try {
                new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.NOT_FOUND).write(source.getWriter());
            } catch (final IOException exp) {
                source.getLogger().log(LogTypeId.WARNING, "Failed to send web answer on TokenLogin");
                throw new RuntimeException(exp);
            }
            return 0;
        }

        final String username;
        if (!(reader.skipWhitespace().hasNext() && "username".equals(reader.readUnquoted()) && reader.skipWhitespace().hasNext() && !(username = reader.readUnquoted()).equals("token"))) {
            try {
                new PlaceholderFileAnswer(file, StandardNamedType.HTML, source.getSender(), source.getRequest(),
                    source.getCore().getEventManager(), this::onUserNotSet).code(ResponseCode.OK).write(source.getWriter());
            } catch (final IOException exp) {
                source.getLogger().log(LogTypeId.WARNING, "Failed to send web answer on TokenLogin");
                throw new RuntimeException(exp);
            }
            return 0;
        }
        
        if (!(reader.skipWhitespace().hasNext() && "token".equals(reader.readUnquoted()) && reader.skipWhitespace().hasNext())) {
            try {
                new PlaceholderFileAnswer(file, StandardNamedType.HTML, source.getSender(), source.getRequest(),
                    source.getCore().getEventManager(), this::onTokenNotSet).code(ResponseCode.OK).write(source.getWriter());
            } catch (final IOException exp) {
                source.getLogger().log(LogTypeId.WARNING, "Failed to send web answer on TokenLogin");
                throw new RuntimeException(exp);
            }
            return 0;
        }
        
        final String token = reader.readUnquoted();
        if (token.length() != 40) {
            try {
                new PlaceholderFileAnswer(file, StandardNamedType.HTML, source.getSender(), source.getRequest(),
                    source.getCore().getEventManager(), this::onTokenInvalid).code(ResponseCode.OK).write(source.getWriter());
            } catch (final IOException exp) {
                source.getLogger().log(LogTypeId.WARNING, "Failed to send web answer on TokenLogin");
                throw new RuntimeException(exp);
            }
            return 0;
        }

        final UUID target = source.getCore().getPlugin().getService().getUniqueId(username);
        if (target == null) {
            try {
                new PlaceholderFileAnswer(file, StandardNamedType.HTML, source.getSender(), source.getRequest(),
                    source.getCore().getEventManager(), this::onUserInvalid).code(ResponseCode.OK).write(source.getWriter());
            } catch (final IOException exp) {
                source.getLogger().log(LogTypeId.WARNING, "Failed to send web answer on TokenLogin");
                throw new RuntimeException(exp);
            }
            return 0;
        }

        final Container<Database> container = source.getCore().getDatabase();
        if (container.isEmpty()) {
            try {
                new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.SERVICE_UNAVAILABLE).write(source.getWriter());
            } catch (final IOException exp) {
                source.getLogger().log(LogTypeId.WARNING, "Failed to send web answer on TokenLogin");
                throw new RuntimeException(exp);
            }
            return 0;
        }
        final Database database = container.get();
        
        try {
            new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.PROCESSING).write(source.getWriter());
        } catch (final IOException exp) {
            source.getLogger().log(LogTypeId.WARNING, "Failed to send web answer on TokenLogin");
            throw new RuntimeException(exp);
        }
        
        final RequestResult result = database.allow(target, token).join();
        
        switch (result) {
        case FAILED:
            try {
                new PlaceholderFileAnswer(file, StandardNamedType.HTML, source.getSender(), source.getRequest(),
                    source.getCore().getEventManager(), this::onTokenInvalid).code(ResponseCode.OK).write(source.getWriter());
            } catch (final IOException exp) {
                source.getLogger().log(LogTypeId.WARNING, "Failed to send web answer on TokenLogin");
                throw new RuntimeException(exp);
            }
            return 0;
        case KNOWN:
            try {
                new PlaceholderFileAnswer(file, StandardNamedType.HTML, source.getSender(), source.getRequest(),
                    source.getCore().getEventManager(), this::onUserKnown).code(ResponseCode.OK).write(source.getWriter());
            } catch (final IOException exp) {
                source.getLogger().log(LogTypeId.WARNING, "Failed to send web answer on TokenLogin");
                throw new RuntimeException(exp);
            }
            return 0;
        default:
            break;
        }
        final String nameOut = source.getCore().getPlugin().getService().getName(target);
        source.getSender().getSession().getData().set("login.success", nameOut == null || nameOut.isBlank() ? username : nameOut,
            IDataType.STRING);
        try {
            new NamedAnswer(StandardNamedType.PLAIN).code(ResponseCode.TEMPORARY_REDIRECT)
                .header("Location", source.getHost() + source.getRequest().getPathAsString()).write(source.getWriter());
        } catch (final IOException exp) {
            source.getLogger().log(LogTypeId.WARNING, "Failed to send web answer on TokenLogin");
            throw new RuntimeException(exp);
        }
        return 0;
    }

    private void onUserNotSet(final PageInjectPlaceholderEvent event) {
        final Template output = new ForkTemplate(event.getTemplate("popup"), "popup-user");
        output.getPlaceholder("error").setValue("User has to be set");
        event.setTemplate(output);
    }

    private void onUserInvalid(final PageInjectPlaceholderEvent event) {
        final Template output = new ForkTemplate(event.getTemplate("popup"), "popup-user");
        output.getPlaceholder("error").setValue("User is invalid");
        event.setTemplate(output);
    }

    private void onUserKnown(final PageInjectPlaceholderEvent event) {
        final Template output = new ForkTemplate(event.getTemplate("popup"), "popup-user");
        output.getPlaceholder("error").setValue("User is already whitelisted");
        event.setTemplate(output);
    }

    private void onTokenNotSet(final PageInjectPlaceholderEvent event) {
        final Template output = new ForkTemplate(event.getTemplate("popup"), "popup-token");
        output.getPlaceholder("error").setValue("Token has to be set");
        event.setTemplate(output);
    }

    private void onTokenInvalid(final PageInjectPlaceholderEvent event) {
        final Template output = new ForkTemplate(event.getTemplate("popup"), "popup-token");
        output.getPlaceholder("error").setValue("Token is invalid");
        event.setTemplate(output);
    }

    private File getFile(final File directory, final String path) {
        File file = new File(directory, path.replace("//", "/"));
        if (!file.exists()) {
            return null;
        }
        if (!file.isFile()) {
            file = new File(file, "index.html");
            if (!file.exists()) {
                return null;
            }
        }
        return file;
    }

}
