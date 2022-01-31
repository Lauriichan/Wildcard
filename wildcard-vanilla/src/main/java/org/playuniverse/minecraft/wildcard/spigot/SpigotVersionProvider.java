package org.playuniverse.minecraft.wildcard.spigot;

import org.bukkit.Bukkit;
import org.playuniverse.minecraft.wildcard.core.util.platform.PlatformType;
import org.playuniverse.minecraft.wildcard.core.util.platform.Version;
import org.playuniverse.minecraft.wildcard.core.util.platform.VersionProvider;

public final class SpigotVersionProvider extends VersionProvider {

    private final Version serverVersion;
    private final Version minecraftVersion;

    public SpigotVersionProvider() {
        this.serverVersion = Version.fromString(Bukkit.getServer().getClass().getPackage().getName().split("\\.", 4)[3]);
        this.minecraftVersion = Version.fromString(Bukkit.getVersion().split(" ")[2].replace(")", ""));
    }

    @Override
    public Version getServerVersion() {
        return serverVersion;
    }

    @Override
    public Version getMinecraftVersion() {
        return minecraftVersion;
    }

    @Override
    public PlatformType getPlatform() {
        return PlatformType.BUKKIT;
    }

}
