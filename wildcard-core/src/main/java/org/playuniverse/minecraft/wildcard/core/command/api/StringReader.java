package org.playuniverse.minecraft.wildcard.core.command.api;

import java.awt.Color;
import java.util.Iterator;
import java.util.function.Predicate;

import com.syntaxphoenix.syntaxapi.logging.color.ColorTools;

public class StringReader implements Iterator<Character> {

    public static final char ESCAPE = '\\';
    public static final char DOUBLE_QUOTE = '"';
    public static final char SINGLE_QUOTE = '\'';
    public static final char HEX_INDICATOR = '#';

    public static boolean isUnquotedCharacter(final char character) {
        return character >= '0' && character <= '9' || character >= 'A' && character <= 'Z' || character >= 'a' && character <= 'z'
            || character == '_' || character == '-' || character == '+' || character == '.';
    }

    public static boolean isDecimalNumber(final char character) {
        return character >= '0' && character <= '9' || character == '-' || character == '+' || character == '.';
    }

    public static boolean isHexNumber(final char character) {
        return character >= 'A' && character <= 'F' || character >= 'a' && character <= 'f' || character >= '0' && character <= '9'
            || character == '-' || character == '+' || character == '.';
    }

    public static boolean isQuote(final char character) {
        return character == DOUBLE_QUOTE || character == SINGLE_QUOTE;
    }

    /*
     * 
     */

    private final String content;
    private final int length;
    private int cursor;

    public StringReader(final String content) {
        this.content = content;
        this.length = content.length();
    }

    /*
     * Getter
     */

    public String getContent() {
        return content;
    }

    public int getCursor() {
        return cursor;
    }

    public int getLength() {
        return length - cursor;
    }

    public int getTotalLength() {
        return length;
    }

    public String getRead() {
        return content.substring(0, cursor);
    }

    public String getRemaining() {
        return content.substring(cursor);
    }

    /*
     * Setter
     */

    public StringReader setCursor(final int cursor) {
        this.cursor = cursor;
        return this;
    }

    /*
     * State
     */

    public boolean hasNext(final int length) {
        return cursor + length <= this.length;
    }

    @Override
    public boolean hasNext() {
        return hasNext(1);
    }

    public char peek() {
        return content.charAt(cursor);
    }

    public char peek(final int offset) {
        return content.charAt(cursor + offset);
    }

    @Override
    public Character next() {
        return content.charAt(cursor++);
    }

    /*
     * Skip
     */

    public StringReader skip() {
        cursor++;
        return this;
    }

    public StringReader skipUntil(final Predicate<Character> predicate) {
        while (hasNext() && predicate.test(peek())) {
            skip();
        }
        return this;
    }

    public StringReader skipWhitespace() {
        return skipUntil(Character::isWhitespace);
    }

    /*
     * Reading
     */

    public String readUntil(final Predicate<Character> predicate) {
        final int start = cursor;
        while (hasNext() && predicate.test(peek())) {
            skip();
        }
        return content.substring(start, cursor);
    }

    public String readUntilUnescaped(final char terminator) {
        final StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        while (hasNext()) {
            final char character = next();
            if (escaped) {
                if (character == terminator || character == ESCAPE) {
                    builder.append(character);
                    escaped = false;
                    continue;
                } else {
                    setCursor(getCursor() - 1);
                    throw new IllegalArgumentException("Invalid escape at " + getCursor());
                }
            }
            if (character == ESCAPE) {
                escaped = true;
                continue;
            } else if (character == terminator) {
                return builder.toString();
            } else {
                builder.append(character);
                continue;
            }
        }
        throw new IllegalArgumentException("Quoted String didn't stop at " + getCursor());
    }

    public String read() {
        if (!hasNext()) {
            return "";
        }
        return isQuote(peek()) ? readQuoted() : readUnquoted();
    }

    public String readUnquoted() {
        return readUntil(StringReader::isUnquotedCharacter);
    }

    public String readQuoted() {
        if (!hasNext()) {
            return "";
        }
        final char quote = peek();
        if (!isQuote(quote)) {
            return "";
        }
        return skip().readUntilUnescaped(quote);
    }

    public String readNumberString() {
        if (!hasNext()) {
            return "";
        }
        if (peek() == HEX_INDICATOR) {
            return skip().readHexString();
        }
        return readDecimalString();
    }

    private String readNumberString(final boolean hex) {
        return hex ? skip().readHexString() : readDecimalString();
    }

    private String readHexString() {
        return readUntil(StringReader::isHexNumber);
    }

    private String readDecimalString() {
        return readUntil(StringReader::isDecimalNumber);
    }

    /*
     * Parsing
     */

    public boolean parseBoolean() {
        final int start = cursor;
        final String content = read();
        if (content.isBlank()) {
            throw new IllegalArgumentException("Boolean can't be parsed -> String is empty");
        }
        if ("true".equalsIgnoreCase(content) || "on".equalsIgnoreCase(content)) {
            return true;
        }
        if ("false".equalsIgnoreCase(content) || "off".equalsIgnoreCase(content)) {
            return false;
        }
        cursor = start;
        throw new IllegalArgumentException("Boolean can't be parsed -> not a boolean");
    }

    public Number parseNumber() {
        if (!hasNext()) {
            return 0;
        }
        try {
            return parseLong();
        } catch (final IllegalArgumentException longStack) {
            try {
                return parseDouble();
            } catch (final IllegalArgumentException doubleStack) {
                final IllegalArgumentException throwable = new IllegalArgumentException("Unable to parse any number", longStack);
                throwable.setStackTrace(doubleStack.getStackTrace());
                throw throwable;
            }
        }
    }

    public byte parseByte() {
        if (!hasNext()) {
            return 0;
        }
        final int start = cursor;
        final boolean hex = peek() == HEX_INDICATOR;
        final String content = readNumberString(hex);
        if (content.isBlank()) {
            return 0;
        }
        try {
            return Byte.parseByte(content, hex ? 16 : 10);
        } catch (final NumberFormatException exp) {
            cursor = start;
            throw new IllegalArgumentException("Can't parse byte", exp);
        }
    }

    public short parseShort() {
        if (!hasNext()) {
            return 0;
        }
        final int start = cursor;
        final boolean hex = peek() == HEX_INDICATOR;
        final String content = readNumberString(hex);
        if (content.isBlank()) {
            return 0;
        }
        try {
            return Short.parseShort(content, hex ? 16 : 10);
        } catch (final NumberFormatException exp) {
            cursor = start;
            throw new IllegalArgumentException("Can't parse short", exp);
        }
    }

    public int parseInt() {
        if (!hasNext()) {
            return 0;
        }
        final int start = cursor;
        final boolean hex = peek() == HEX_INDICATOR;
        final String content = readNumberString(hex);
        if (content.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(content, hex ? 16 : 10);
        } catch (final NumberFormatException exp) {
            cursor = start;
            throw new IllegalArgumentException("Can't parse int", exp);
        }
    }

    public long parseLong() {
        if (!hasNext()) {
            return 0;
        }
        final int start = cursor;
        final boolean hex = peek() == HEX_INDICATOR;
        final String content = readNumberString(hex);
        if (content.isBlank()) {
            return 0;
        }
        try {
            return Long.parseLong(content, hex ? 16 : 10);
        } catch (final NumberFormatException exp) {
            cursor = start;
            throw new IllegalArgumentException("Can't parse long", exp);
        }
    }

    public float parseFloat() {
        if (!hasNext()) {
            return 0f;
        }
        final int start = cursor;
        final String content = readDecimalString();
        if (content.isBlank()) {
            return 0f;
        }
        try {
            return Float.parseFloat(content);
        } catch (final NumberFormatException exp) {
            cursor = start;
            throw new IllegalArgumentException("Can't parse float", exp);
        }
    }

    public double parseDouble() {
        if (!hasNext()) {
            return 0d;
        }
        final int start = cursor;
        final String content = readDecimalString();
        if (content.isBlank()) {
            return 0d;
        }
        try {
            return Double.parseDouble(content);
        } catch (final NumberFormatException exp) {
            cursor = start;
            throw new IllegalArgumentException("Can't parse double", exp);
        }
    }

    public Color parseColor() {
        if (!hasNext()) {
            return null;
        }
        final int start = cursor;
        final String content = read();
        try {
            final Color output = ColorTools.hex2rgb(content);
            if (output == null) {
                cursor = start;
                throw new IllegalArgumentException("Too short or too long to be a hex color");
            }
            return output;
        } catch (final NumberFormatException exp) {
            cursor = start;
            throw new IllegalArgumentException("Unparsable Hex color", exp);
        }
    }

    /*
     * Tests
     */

    protected boolean test(final ReadTest test) {
        final int start = cursor;
        try {
            test.test(this);
            cursor = start;
            return true;
        } catch (final IllegalArgumentException exp) {
            cursor = start;
            return false;
        }
    }

    public boolean testQuoted() {
        return test(StringReader::readQuoted);
    }

    public boolean testBoolean() {
        return test(StringReader::parseBoolean);
    }

    public boolean testNumber() {
        return test(StringReader::parseNumber);
    }

    public boolean testByte() {
        return test(StringReader::parseByte);
    }

    public boolean testShort() {
        return test(StringReader::parseShort);
    }

    public boolean testInt() {
        return test(StringReader::parseInt);
    }

    public boolean testLong() {
        return test(StringReader::parseLong);
    }

    public boolean testFloat() {
        return test(StringReader::parseLong);
    }

    public boolean testDouble() {
        return test(StringReader::parseLong);
    }

    public boolean testColor() {
        return test(StringReader::parseColor);
    }

}
