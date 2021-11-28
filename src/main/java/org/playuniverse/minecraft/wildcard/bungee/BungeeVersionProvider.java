package org.playuniverse.minecraft.wildcard.bungee;

import org.playuniverse.minecraft.wildcard.core.util.platform.PlatformType;
import org.playuniverse.minecraft.wildcard.core.util.platform.Version;
import org.playuniverse.minecraft.wildcard.core.util.platform.VersionProvider;

import net.md_5.bungee.api.ProxyServer;

public final class BungeeVersionProvider extends VersionProvider {

    private final Version serverVersion;

    public BungeeVersionProvider() {
        final String[] version = ProxyServer.getInstance().getVersion().split(":", 3)[2].split("-", 3);
        this.serverVersion = Version.fromString(version[0].replace('.', '_') + '_' + version[1]);
    }

    @Override
    public Version getServerVersion() {
        return serverVersion;
    }

    @Override
    public Version getMinecraftVersion() {
        return serverVersion;
    }

    @Override
    public PlatformType getPlatform() {
        return PlatformType.BUNGEECORD;
    }

}
