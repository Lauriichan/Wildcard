package me.lauriichan.minecraft.wildcard.bungee.component;

import me.lauriichan.minecraft.wildcard.bungee.BungeeAdapter;
import me.lauriichan.minecraft.wildcard.core.message.IPlatformComponentAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import net.md_5.bungee.api.chat.BaseComponent;

public class BungeeComponentAdapter implements IPlatformComponentAdapter<BaseComponent> {

    private final BungeeAdapter bungeeAdapter;

    public BungeeComponentAdapter(BungeeAdapter bungeeAdapter) {
        this.bungeeAdapter = bungeeAdapter;
    }

    public BungeeAdapter getBungeeAdapter() {
        return bungeeAdapter;
    }

    @Override
    public PlatformComponent create() {
        return new BungeeComponent(this);
    }

    @Override
    public BaseComponent asHandle(PlatformComponent component) {
        return ((BungeeComponent) component).getHandle();
    }

    @Override
    public BaseComponent[] asHandle(PlatformComponent... components) {
        BaseComponent[] output = new BaseComponent[components.length];
        for(int i = 0; i < components.length; i++) {
            output[i] = ((BungeeComponent) components[i]).getHandle();
        }
        return output;
    }

}
