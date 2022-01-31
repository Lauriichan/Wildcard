package me.lauriichan.minecraft.wildcard.core.data.storage.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public final class UUIDHelper {

    private UUIDHelper() {}

    public static UUID toUniqueId(final byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new UUID(buffer.getLong(0), buffer.getLong(8));
    }

    public static byte[] fromUniqueId(final UUID uniqueId) {
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(0, uniqueId.getMostSignificantBits());
        buffer.putLong(8, uniqueId.getLeastSignificantBits());
        return buffer.array();
    }

    public static UUID fromString(final String string) {
        try {
            return UUID.fromString(string);
        } catch (final IllegalArgumentException exp) {
            return null;
        }
    }

}
