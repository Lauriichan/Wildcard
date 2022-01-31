package org.playuniverse.minecraft.wildcard.core.util;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.data.setting.json.JsonIO;

import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.utils.java.Streams;

public final class MojangProfileService {

    private MojangProfileService() {}

    public static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    public static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    public static UUID fromShort(final String uid) {
        return UUID.fromString(uid.substring(0, 8) + "-" + uid.substring(8, 12) + "-" + uid.substring(12, 16) + "-" + uid.substring(16, 20)
            + "-" + uid.substring(20, 32));
    }

    public static String asShort(final UUID id) {
        return id.toString().replace("-", "");
    }

    public static UUID getUniqueId(final String name) {
        return fromShort(getUniqueIdAsString(name));
    }

    public static String getUniqueIdAsString(final String name) {
        try {
            final URL url = new URL(String.format(UUID_URL, name));
            final String jsonString = Streams.toString(url.openStream());
            if (!jsonString.isEmpty()) {
                final JsonObject object = (JsonObject) JsonIO.PARSER.fromString(jsonString);
                return object.get("id").getValue().toString();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getName(final UUID uniqueId) {
        return getName(asShort(uniqueId));
    }

    public static String getName(final String uniqueId) {
        try {
            final URL url = new URL(String.format(PROFILE_URL, uniqueId));
            final String jsonString = Streams.toString(url.openStream());
            if (!jsonString.isEmpty()) {
                final JsonObject object = (JsonObject) JsonIO.PARSER.fromString(jsonString);
                return object.get("name").getValue().toString();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}