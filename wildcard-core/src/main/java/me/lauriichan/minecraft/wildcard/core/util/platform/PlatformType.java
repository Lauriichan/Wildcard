package me.lauriichan.minecraft.wildcard.core.util.platform;

public interface PlatformType {

    public static final PlatformType FORGE = new PlatformTypeImpl("Forge", false);
    public static final PlatformType FABRIC = new PlatformTypeImpl("Fabric", false);
    public static final PlatformType SPONGE = new PlatformTypeImpl("Sponge", false);

    public static final PlatformType SPIGOT = new PlatformTypeImpl("Spigot", true);
    public static final PlatformType BUNGEECORD = new PlatformTypeImpl("Bungeecord", true);

    boolean isVanilla();

    String name();

}
