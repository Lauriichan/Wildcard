package org.playuniverse.minecraft.wildcard.core.util;

import java.awt.Color;

import org.playuniverse.minecraft.wildcard.core.IWildcardAdapter;

import com.syntaxphoenix.syntaxapi.logging.color.ColorTools;

import net.md_5.bungee.api.chat.TextComponent;

public final class ComponentParser {

    public static final Color DEFAULT_COLOR = Format.GRAY.getColor();

    private final IWildcardAdapter adapter;

    public ComponentParser(final IWildcardAdapter adapter) {
        this.adapter = adapter;
    }

    public TextComponent[] parse(final String richString) {
        return parse(richString, DEFAULT_COLOR);
    }

    public TextComponent[] parse(final String richString, final Format defaultColor) {
        return parse(richString, defaultColor == null || defaultColor.isFormat() ? DEFAULT_COLOR : defaultColor.getColor());
    }

    public TextComponent[] parse(final String richString, final Color defaultColor) {
        final DynamicArray<TextComponent> array = new DynamicArray<>();
        TextComponent component = new TextComponent();
        Color color = defaultColor;
        adapter.applyColor(component, color);
        StringBuilder builder = new StringBuilder();
        final char[] chars = richString.toCharArray();
        for (int index = 0; index < chars.length; index++) {
            final char chr = chars[index];
            if (chr == '&') {
                if (index + 1 >= chars.length) {
                    break;
                }
                if (chars[index + 1] != '#' || index + 7 >= chars.length) {
                    final Format format = Format.byChar(chars[index + 1]);
                    if (format == null) {
                        builder.append(chr);
                        continue;
                    }
                    index += 1;
                    if (builder.length() > 0) {
                        final TextComponent old = component;
                        component = new TextComponent(old);
                        old.setText(builder.toString());
                        builder = new StringBuilder();
                        array.add(old);
                    }
                    if (format.isFormat()) {
                        switch (format) {
                        case BOLD:
                            component.setBold(true);
                            break;
                        case ITALIC:
                            component.setItalic(true);
                            break;
                        case UNDERLINE:
                            component.setUnderlined(true);
                            break;
                        case STRIKETHROUGH:
                            component.setStrikethrough(true);
                            break;
                        case MAGIC:
                            component.setObfuscated(true);
                            break;
                        case RESET:
                            component = new TextComponent();
                            adapter.applyColor(component, defaultColor);
                            break;
                        default:
                            break;
                        }
                        continue;
                    }
                    component = new TextComponent();
                    adapter.applyColor(component, format.getColor());
                    continue;
                }
                StringBuilder hex = new StringBuilder();
                for (int idx = index + 2; idx <= index + 7; idx++) {
                    final char hch = chars[idx];
                    if (hch >= 'A' && hch <= 'Z') {
                        hex.append((char) (hch + 32));
                        continue;
                    }
                    if (hch >= 'a' && hch <= 'z' || hch >= '0' && hch <= '9') {
                        hex.append(hch);
                        continue;
                    }
                    break;
                }
                if (!(hex.length() == 6 || hex.length() == 3)) {
                    builder.append(chr);
                    continue;
                }
                String hexCode = hex.toString();
                if (hexCode.length() == 3) {
                    index -= 3;
                    hex = new StringBuilder();
                    hex.append(hexCode.charAt(0)).append(hexCode.charAt(0));
                    hex.append(hexCode.charAt(1)).append(hexCode.charAt(1));
                    hex.append(hexCode.charAt(2)).append(hexCode.charAt(2));
                    hexCode = hex.toString();
                }
                index += 7;
                if (builder.length() > 0) {
                    final TextComponent old = component;
                    component = new TextComponent(old);
                    old.setText(builder.toString());
                    builder = new StringBuilder();
                    array.add(old);
                }
                color = ColorTools.hex2rgb(hexCode);
                adapter.applyColor(component, color);
                continue;
            }
            builder.append(chr);
        }
        if (builder.length() > 0) {
            component.setText(builder.toString());
            array.add(component);
        }
        return array.asArray(TextComponent[]::new);
    }

}
