package me.lauriichan.minecraft.wildcard.core.message;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import com.syntaxphoenix.syntaxapi.logging.color.ColorTools;

public enum Format {

    BLACK('0', "000000"),
    DARK_BLUE('1', "0000AA"),
    DARK_GREEN('2', "00AA00"),
    DARK_AQUA('3', "00AAAA"),
    DARK_RED('4', "AA0000"),
    DARK_PURPLE('5', "AA00AA"),
    GOLD('6', "FFAA00"),
    GRAY('7', "AAAAAA"),
    DARK_GRAY('8', "555555"),
    BLUE('9', "5555FF"),
    GREEN('a', "55FF55"),
    AQUA('b', "55FFFF"),
    RED('c', "FF5555"),
    LIGHT_PURPLE('d', "FF55FF"),
    YELLOW('e', "FFFF55"),
    WHITE('f', "FFFFFF"),
    MAGIC('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),
    RESET('r');

    public static final char COLOR_CODE = '&';
    
    private static final Format[] FORMAT_VALUES;
    private static final HashMap<Character, Format> FORMATS = new HashMap<>();

    private final boolean format;
    private final Color color;
    private final char code;

    Format(final char code, final String hex) {
        this.format = false;
        this.color = ColorTools.hex2rgb(hex);
        this.code = code;
    }

    Format(final char code) {
        this.format = true;
        this.color = null;
        this.code = code;
    }

    public boolean isFormat() {
        return format;
    }

    public boolean isReset() {
        return this == RESET;
    }

    public Color getColor() {
        return color;
    }

    public char getCode() {
        return code;
    }
    
    public static Format[] formatValues() {
        Format[] output = new Format[FORMAT_VALUES.length];
        System.arraycopy(FORMAT_VALUES, 0, output, 0, FORMAT_VALUES.length);
        return output;
    }

    public static Format byChar(char code) {
        return FORMATS.get(Character.toLowerCase(code));
    }
    
    static {
        ArrayList<Format> formats = new ArrayList<>();
        for(Format format : Format.values()) {
            FORMATS.put(format.code, format);
            if(format.isFormat()) {
                formats.add(format);
            }
        }
        FORMAT_VALUES = formats.toArray(Format[]::new);
    }

}
