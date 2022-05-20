package me.lauriichan.minecraft.wildcard.sponge.component;

import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class SpongeComponentAdapter implements IPlatformComponentAdapter<Component> {

    @Override
    public PlatformComponent create() {
        return new SpongeComponent(this);
    }

    @Override
    public Component asHandle(PlatformComponent component) {
        return ((SpongeComponent) component).getHandle();
    }

    @Override
    public Component[] asHandle(PlatformComponent... components) {
        TextComponent component = Component.empty();
        for (int i = 0; i < components.length; i++) {
            component.append(asHandle(components[i]));
        }
        return new Component[] {
            component
        };
    }

}
