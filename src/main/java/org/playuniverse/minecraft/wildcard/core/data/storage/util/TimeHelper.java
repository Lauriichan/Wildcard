package org.playuniverse.minecraft.wildcard.core.data.storage.util;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeHelper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-HH_mm-ss");

    private TimeHelper() {}

    public static OffsetDateTime fromString(String string) {
        return string == null ? null : OffsetDateTime.parse(string, FORMATTER);
    }

    public static String toString(OffsetDateTime time) {
        return time == null ? null : FORMATTER.format(time);
    }

}
