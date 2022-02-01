package me.lauriichan.minecraft.wildcard.mixin.api;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;

public final class FabricMixin {

    public static final Container<IMinecraftServerAccess> SERVER = Container.of();
    public static final Container<IPlayerJoinCallback> JOIN_CALLBACK = Container.of();

    private FabricMixin() {
        throw new UnsupportedOperationException("Constant class");
    }

    public static MinecraftDedicatedServer server() {
        return (MinecraftDedicatedServer) SERVER.get();
    }

}
