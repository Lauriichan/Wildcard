package me.lauriichan.minecraft.wildcard.sponge;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;

import com.syntaxphoenix.syntaxapi.utils.java.Strings;

import me.lauriichan.minecraft.wildcard.core.util.platform.PlatformType;
import me.lauriichan.minecraft.wildcard.core.util.platform.Version;
import me.lauriichan.minecraft.wildcard.core.util.platform.VersionProvider;

public final class SpongeVersionProvider extends VersionProvider {

    private static final TreeMap<Integer, Version> MAP = new TreeMap<>();

    static {
        MAP.put(2586, new Version(1, 16, 5));
        MAP.put(2724, new Version(1, 17, 0));
        MAP.put(2730, new Version(1, 17, 1));
        MAP.put(2860, new Version(1, 18, 0));
        MAP.put(2865, new Version(1, 18, 1));
    }

    private final Version version;

    public SpongeVersionProvider() {
        MinecraftVersion minecraft = Sponge.platform().minecraftVersion();
        Version tmp;
        if (Strings.isNumeric(minecraft.name())) {
            Entry<Integer, Version> entry = MAP.floorEntry(minecraft.dataVersion().orElse(2586));
            if (entry != null) {
                tmp = entry.getValue();
            }
        }
        tmp = Version.fromString(minecraft.name());
        this.version = tmp;
    }

    @Override
    public Version getServerVersion() {
        return version;
    }

    @Override
    public Version getMinecraftVersion() {
        return version;
    }

    @Override
    public PlatformType getPlatform() {
        return PlatformType.SPONGE;
    }

}
