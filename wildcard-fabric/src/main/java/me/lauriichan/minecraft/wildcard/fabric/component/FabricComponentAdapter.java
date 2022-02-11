package me.lauriichan.minecraft.wildcard.fabric.component;

import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.LiteralText;

public final class FabricComponentAdapter implements IPlatformComponentAdapter<MutableText> {

    @Override
    public MutableText asHandle(PlatformComponent component) {
        return ((FabricComponent) component).getHandle();
    }

    @Override
    public MutableText[] asHandle(PlatformComponent... components) {
        LiteralText text = new LiteralText("");
        for (PlatformComponent component : components) {
            text.append(asHandle(component));
        }
        return new MutableText[] {
            text
        };
    }

    @Override
    public PlatformComponent create() {
        return new FabricComponent(this);
    }

}
