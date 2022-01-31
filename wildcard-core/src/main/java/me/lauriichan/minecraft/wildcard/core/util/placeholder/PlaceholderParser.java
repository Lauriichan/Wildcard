package me.lauriichan.minecraft.wildcard.core.util.placeholder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderParser {

    public static final Pattern PLACEHOLDER = Pattern.compile("(?<placeholder>\\$\\(\\\"(?<key>[a-zA-Z0-9\\_\\-\\. ]+)\\\"\\))");

    private PlaceholderParser() {}

    public static Placeholder[] parse(final String data) {
        final DefaultPlaceholderStore store = new DefaultPlaceholderStore();
        parse(store, data);
        return store.placeholderArray();
    }

    public static void parse(final PlaceholderStore store, final String data) {
        final Matcher matcher = PLACEHOLDER.matcher(data);
        while (matcher.find()) {
            store.setPlaceholder(new Placeholder(matcher.group("placeholder"), matcher.group("key")));
        }
    }

    public static String apply(final PlaceableStore store, final String data) {
        String output = data;
        for (final Placeable placeable : store.placeableArray()) {
            output = output.replace(placeable.getPlaceKey(), placeable.getPlaceValue());
        }
        return output;
    }

}
