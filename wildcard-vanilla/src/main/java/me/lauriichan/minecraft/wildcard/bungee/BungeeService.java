package me.lauriichan.minecraft.wildcard.bungee;

import java.util.UUID;

import me.lauriichan.minecraft.wildcard.core.MessageAdapter;
import me.lauriichan.minecraft.wildcard.core.ServiceAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponentParser;
import me.lauriichan.minecraft.wildcard.core.settings.PluginSettings;

public final class BungeeService extends ServiceAdapter {
    
    private final PlatformComponentParser parser;

    public BungeeService(final PlatformComponentParser parser, final PluginSettings settings) {
        super(settings);
        this.parser = parser;
    }

    @Override
    protected MessageAdapter buildAdapter(final UUID uniqueId) {
        return new BungeeSender(parser, uniqueId);
    }

}
