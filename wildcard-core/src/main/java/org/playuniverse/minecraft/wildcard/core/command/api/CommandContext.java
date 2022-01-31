package org.playuniverse.minecraft.wildcard.core.command.api;

public class CommandContext<S> {

    private final S source;
    private final StringReader reader;

    public CommandContext(final S source, final String input) {
        this(source, new StringReader(input));
    }

    public CommandContext(final S source, final StringReader reader) {
        this.source = source;
        this.reader = reader;
    }

    public S getSource() {
        return source;
    }

    public StringReader getReader() {
        return reader;
    }

}
