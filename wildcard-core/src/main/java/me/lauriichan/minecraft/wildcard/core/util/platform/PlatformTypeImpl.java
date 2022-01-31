package me.lauriichan.minecraft.wildcard.core.util.platform;

final class PlatformTypeImpl implements PlatformType {

    private final boolean vanilla;
    private final String name;

    public PlatformTypeImpl(String name, boolean vanilla) {
        this.name = name;
        this.vanilla = vanilla;
    }

    @Override
    public boolean isVanilla() {
        return vanilla;
    }

    @Override
    public String name() {
        return name;
    }

}
