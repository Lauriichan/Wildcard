package me.lauriichan.minecraft.wildcard.sponge.component;

import org.spongepowered.api.text.Text;

import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;

public class SpongeComponentAdapter implements IPlatformComponentAdapter<Text> {

    @Override
    public PlatformComponent create() {
        return new SpongeComponent(this);
    }

    @Override
    public Text asHandle(PlatformComponent component) {
        return ((SpongeComponent) component).getHandle();
    }

    @Override
    public Text[] asHandle(PlatformComponent... components) {
        Text.Builder builder = Text.builder();
        for (int i = 0; i < components.length; i++) {
            builder.append(asHandle(components[i]));
        }
        return new Text[] {
            builder.build()
        };
    }

}
