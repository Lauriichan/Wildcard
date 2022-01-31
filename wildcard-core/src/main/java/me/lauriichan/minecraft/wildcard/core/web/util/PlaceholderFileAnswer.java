package me.lauriichan.minecraft.wildcard.core.web.util;

import java.io.File;
import java.util.function.Consumer;

// import com.syntaxphoenix.discord.management.plugin.panel.server.WebSender;
import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.net.http.FileAnswer;
import com.syntaxphoenix.syntaxapi.net.http.NamedType;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;
import com.syntaxphoenix.syntaxapi.net.http.SimpleFileAnswer;

import me.lauriichan.minecraft.wildcard.core.util.Awaiter;
import me.lauriichan.minecraft.wildcard.core.util.placeholder.PlaceholderParser;
import me.lauriichan.minecraft.wildcard.core.util.placeholder.TemplateParser;
import me.lauriichan.minecraft.wildcard.core.web.WebSender;

public class PlaceholderFileAnswer extends SimpleFileAnswer {

    private final WebSender sender;
    private final EventManager manager;
    private final ReceivedRequest data;

    private final Consumer<PageInjectPlaceholderEvent> callback;

    private boolean ready = false;

    public PlaceholderFileAnswer(final File file, final NamedType type, final WebSender sender, final ReceivedRequest data,
        final EventManager manager) {
        super(file, type);
        this.manager = manager;
        this.sender = sender;
        this.data = data;
        this.ready = true;
        this.callback = null;
        refresh();
    }

    public PlaceholderFileAnswer(final File file, final NamedType type, final WebSender sender, final ReceivedRequest data,
        final EventManager manager, final Consumer<PageInjectPlaceholderEvent> callback) {
        super(file, type);
        this.manager = manager;
        this.sender = sender;
        this.data = data;
        this.ready = true;
        this.callback = callback;
        refresh();
    }

    @Override
    public FileAnswer refresh() {
        if (ready) {
            return super.refresh();
        }
        return this;
    }

    @Override
    public String readFile() {
        String fileData = super.readFile();

        final PageInjectPlaceholderEvent event = new PageInjectPlaceholderEvent(type, sender, data);

        TemplateParser.parse(event, fileData);
        fileData = TemplateParser.strip(event, fileData);
        PlaceholderParser.parse(event, fileData);

        Awaiter.of(manager.call(event)).await();

        if (callback != null) {
            callback.accept(event);
        }

        fileData = PlaceholderParser.apply(event, fileData);
        return TemplateParser.apply(event, fileData);
    }

}
