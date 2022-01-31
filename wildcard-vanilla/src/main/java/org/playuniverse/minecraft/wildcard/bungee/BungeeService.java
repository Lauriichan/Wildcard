package org.playuniverse.minecraft.wildcard.bungee;

import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.MessageAdapter;
import org.playuniverse.minecraft.wildcard.core.ServiceAdapter;
import org.playuniverse.minecraft.wildcard.core.message.PlatformComponentParser;
import org.playuniverse.minecraft.wildcard.core.settings.PluginSettings;

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
