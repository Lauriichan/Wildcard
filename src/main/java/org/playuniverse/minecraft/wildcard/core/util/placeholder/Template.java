package org.playuniverse.minecraft.wildcard.core.util.placeholder;

public class Template implements PlaceholderStore {

    private final String original;

    private final String key;
    private final String content;

    private final DefaultPlaceholderStore store = new DefaultPlaceholderStore();

    public Template(final String original, final String key, final String content) {
        this.original = original;
        this.key = key;
        this.content = content;
        PlaceholderParser.parse(store, content);
    }

    protected String getOriginal() {
        return original;
    }

    public String getReplaceContent() {
        return PlaceholderParser.apply(store, content);
    }

    public String getContent() {
        return content;
    }

    public String getKey() {
        return key;
    }

    @Override
    public void setPlaceholder(final Placeholder value) {}

    @Override
    public Placeholder getPlaceholder(final String key) {
        return store.getPlaceholder(key);
    }

    @Override
    public Placeholder[] placeholderArray() {
        return store.placeholderArray();
    }

}
