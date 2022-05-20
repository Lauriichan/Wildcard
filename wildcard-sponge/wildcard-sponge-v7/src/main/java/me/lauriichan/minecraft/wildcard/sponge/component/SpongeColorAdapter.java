package me.lauriichan.minecraft.wildcard.sponge.component;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public final class SpongeColorAdapter {

    public static final SpongeColorAdapter ADAPTER = new SpongeColorAdapter();

    private final Map<Color, TextColor> colorMap = fillColors();
    private final Color[] colors = colorMap.keySet().toArray(new Color[colorMap.size()]);

    private SpongeColorAdapter() {}

    public final TextColor getNearestColor(final Color color) {
        if (color == null) {
            return null;
        }
        int target = -1;
        long nearest = Long.MAX_VALUE;
        for (int index = 0; index < colors.length; index++) {
            final long distance = distanceSquared(color, colors[index]);
            if (distance >= nearest) {
                continue;
            }
            target = index;
            nearest = distance;
        }
        return target == -1 ? null : colorMap.get(colors[target]);
    }

    private Map<Color, TextColor> fillColors() {
        final HashMap<Color, TextColor> colors = new HashMap<>();
        colors.put(new Color(255, 85, 255), TextColors.LIGHT_PURPLE);
        colors.put(new Color(170, 0, 170), TextColors.DARK_PURPLE);
        colors.put(new Color(0, 170, 170), TextColors.DARK_AQUA);
        colors.put(new Color(0, 170, 0), TextColors.DARK_GREEN);
        colors.put(new Color(85, 85, 85), TextColors.DARK_GRAY);
        colors.put(new Color(255, 255, 85), TextColors.YELLOW);
        colors.put(new Color(0, 0, 170), TextColors.DARK_BLUE);
        colors.put(new Color(255, 255, 255), TextColors.WHITE);
        colors.put(new Color(170, 0, 0), TextColors.DARK_RED);
        colors.put(new Color(170, 170, 170), TextColors.GRAY);
        colors.put(new Color(85, 255, 255), TextColors.AQUA);
        colors.put(new Color(85, 255, 85), TextColors.GREEN);
        colors.put(new Color(255, 255, 85), TextColors.RED);
        colors.put(new Color(255, 170, 0), TextColors.GOLD);
        colors.put(new Color(85, 85, 255), TextColors.BLUE);
        colors.put(new Color(0, 0, 0), TextColors.BLACK);
        return Collections.unmodifiableMap(colors);
    }

    private long distanceSquared(final Color color1, final Color color2) {
        final int red1 = color1.getRed();
        final int red2 = color1.getRed();
        final int rmean = red1 + red2 >> 1;
        final int r = red1 - red2;
        final int g = color1.getGreen() - color2.getGreen();
        final int b = color1.getBlue() - color2.getBlue();
        return ((512 + rmean) * r * r >> 8) + 4 * g * g + ((767 - rmean) * b * b >> 8);
    }

}
