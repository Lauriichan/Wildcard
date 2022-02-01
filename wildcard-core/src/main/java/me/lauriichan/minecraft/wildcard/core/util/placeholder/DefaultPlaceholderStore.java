package me.lauriichan.minecraft.wildcard.core.util.placeholder;

import java.util.ArrayList;

public class DefaultPlaceholderStore implements PlaceholderStore {

    private final ArrayList<Placeholder> placeholders = new ArrayList<>();

    @Override
    public void setPlaceholder(final Placeholder value) {
        if (hasPlaceholder(value.getKey())) {
            return;
        }
        placeholders.add(value);
    }

    public boolean hasPlaceholder(final String key) {
        return placeholders.stream().anyMatch(template -> template.getKey().equals(key));
    }

    @Override
    public Placeholder getPlaceholder(final String key) {
        return placeholders.stream().filter(template -> template.getKey().equals(key)).findFirst().orElse(null);
    }

    @Override
    public Placeholder[] placeholderArray() {
        return placeholders.toArray(new Placeholder[placeholders.size()]);
    }

}
