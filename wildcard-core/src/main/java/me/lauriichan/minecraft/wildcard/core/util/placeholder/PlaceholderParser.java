package me.lauriichan.minecraft.wildcard.core.util.placeholder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderParser {

    public static final Pattern FUNCTION_PLACEHOLDER = Pattern.compile("(?<placeholder>\\$\\(\\\"(?<key>[a-zA-Z0-9\\_\\-\\. ]+)\\\"\\))");
    public static final Pattern MESSAGE_PLACEHOLDER = Pattern.compile("(?<placeholder>\\$(?<key>[a-zA-Z0-9\\_\\-\\.]+))");

    private PlaceholderParser() {}

    public static Placeholder[] parseFunction(String data) {
        DefaultPlaceholderStore store = new DefaultPlaceholderStore();
        parseFunction(store, data);
        return store.placeholderArray();
    }

    public static Placeholder[] parseMessage(String data) {
        DefaultPlaceholderStore store = new DefaultPlaceholderStore();
        parseMessage(store, data);
        return store.placeholderArray();
    }

    public static void parseFunction(PlaceholderStore store, String data) {
        Matcher matcher = FUNCTION_PLACEHOLDER.matcher(data);
        while (matcher.find()) {
            store.setPlaceholder(new Placeholder(matcher.group("placeholder"), matcher.group("key")));
        }
    }
    
    public static void parseMessage(PlaceholderStore store, String data) {
        Matcher matcher = MESSAGE_PLACEHOLDER.matcher(data);
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
