package org.playuniverse.minecraft.wildcard.core.util.platform;

public abstract class VersionProvider {

    public abstract Version getServerVersion();

    public abstract Version getMinecraftVersion();

    public abstract PlatformType getPlatform();

}
