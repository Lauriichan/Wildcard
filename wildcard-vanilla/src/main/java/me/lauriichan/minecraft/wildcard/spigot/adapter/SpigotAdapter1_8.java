package me.lauriichan.minecraft.wildcard.spigot.adapter;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.lauriichan.minecraft.wildcard.spigot.SpigotAdapter;
import net.md_5.bungee.api.chat.TextComponent;

public final class SpigotAdapter1_8 extends SpigotAdapter {

    private final Map<Color, ChatColor> colorMap = fillColors();
    private final Color[] colors = colorMap.keySet().toArray(new Color[colorMap.size()]);

    @Override
    public String getServerName() {
        return Bukkit.getServer().getName();
    }

    @Override
    public void applyColor(final TextComponent component, final Color color) {
        final ChatColor output = getNearestColor(color);
        if (output == null) {
            return;
        }
        component.setColor(net.md_5.bungee.api.ChatColor.getByChar(output.getChar()));
    }

    /*
     * Helper
     */

    private Map<Color, ChatColor> fillColors() {
        final HashMap<Color, ChatColor> colors = new HashMap<>();
        colors.put(new Color(255, 85, 255), ChatColor.LIGHT_PURPLE);
        colors.put(new Color(170, 0, 170), ChatColor.DARK_PURPLE);
        colors.put(new Color(0, 170, 170), ChatColor.DARK_AQUA);
        colors.put(new Color(0, 170, 0), ChatColor.DARK_GREEN);
        colors.put(new Color(85, 85, 85), ChatColor.DARK_GRAY);
        colors.put(new Color(255, 255, 85), ChatColor.YELLOW);
        colors.put(new Color(0, 0, 170), ChatColor.DARK_BLUE);
        colors.put(new Color(255, 255, 255), ChatColor.WHITE);
        colors.put(new Color(170, 0, 0), ChatColor.DARK_RED);
        colors.put(new Color(170, 170, 170), ChatColor.GRAY);
        colors.put(new Color(85, 255, 255), ChatColor.AQUA);
        colors.put(new Color(85, 255, 85), ChatColor.GREEN);
        colors.put(new Color(255, 255, 85), ChatColor.RED);
        colors.put(new Color(255, 170, 0), ChatColor.GOLD);
        colors.put(new Color(85, 85, 255), ChatColor.BLUE);
        colors.put(new Color(0, 0, 0), ChatColor.BLACK);
        return Collections.unmodifiableMap(colors);
    }

    private ChatColor getNearestColor(final Color color) {
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
