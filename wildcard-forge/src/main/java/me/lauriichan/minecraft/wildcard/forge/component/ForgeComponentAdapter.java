package me.lauriichan.minecraft.wildcard.forge.component;

import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public final class ForgeComponentAdapter implements IPlatformComponentAdapter<ITextComponent> {

    @Override
    public PlatformComponent create() {
        return new ForgeComponent(this);
    }

    @Override
    public ITextComponent asHandle(PlatformComponent component) {
        return ((ForgeComponent) component).getHandle();
    }

    @Override
    public ITextComponent[] asHandle(PlatformComponent... components) {
        StringTextComponent component = new StringTextComponent("");
        for (int i = 0; i < components.length; i++) {
            component.append(asHandle(components[i]));
        }
        return new ITextComponent[] {
            component
        };
    }

}
