package me.lauriichan.minecraft.wildcard.forge;

import java.util.TreeMap;
import java.util.Map.Entry;

import com.mojang.bridge.game.GameVersion;

import me.lauriichan.minecraft.wildcard.core.util.platform.PlatformType;
import me.lauriichan.minecraft.wildcard.core.util.platform.Version;
import me.lauriichan.minecraft.wildcard.core.util.platform.VersionProvider;
import net.minecraft.util.MinecraftVersion;

public final class ForgeVersionProvider extends VersionProvider {

    private static final TreeMap<Integer, Version> MAP = new TreeMap<>();

    static {
        MAP.put(2586, new Version(1, 16, 5));
        MAP.put(2724, new Version(1, 17, 0));
        MAP.put(2730, new Version(1, 17, 1));
        MAP.put(2860, new Version(1, 18, 0));
        MAP.put(2865, new Version(1, 18, 1));
    }

    private final Version version;

    public ForgeVersionProvider() {
        Version tmp = null;
        try {
            GameVersion version = MinecraftVersion.tryDetectVersion();
            Entry<Integer, Version> entry = MAP.floorEntry(version.getProtocolVersion());
            if (entry != null) {
                tmp = entry.getValue();
            }
        } catch (IllegalStateException ignore) {
        }
        if (tmp == null) {
            tmp = new Version();
        }
        this.version = tmp;
    }

    @Override
    public Version getMinecraftVersion() {
        return version;
    }

    @Override
    public Version getServerVersion() {
        return version;
    }

    @Override
    public PlatformType getPlatform() {
        return PlatformType.FORGE;
    }

}
