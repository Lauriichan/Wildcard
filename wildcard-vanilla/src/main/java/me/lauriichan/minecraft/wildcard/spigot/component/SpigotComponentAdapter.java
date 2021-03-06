package me.lauriichan.minecraft.wildcard.spigot.component;

import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.spigot.SpigotAdapter;
import net.md_5.bungee.api.chat.BaseComponent;

public class SpigotComponentAdapter implements IPlatformComponentAdapter<BaseComponent> {

    private final SpigotAdapter spigotAdapter;

    public SpigotComponentAdapter(SpigotAdapter spigotAdapter) {
        this.spigotAdapter = spigotAdapter;
    }

    public SpigotAdapter getSpigotAdapter() {
        return spigotAdapter;
    }

    @Override
    public PlatformComponent create() {
        return new SpigotComponent(this);
    }

    @Override
    public BaseComponent asHandle(PlatformComponent component) {
        return ((SpigotComponent) component).getHandle();
    }

    @Override
    public BaseComponent[] asHandle(PlatformComponent... components) {
        BaseComponent[] output = new BaseComponent[components.length];
        for(int i = 0; i < components.length; i++) {
            output[i] = ((SpigotComponent) components[i]).getHandle();
        }
        return output;
    }

}
