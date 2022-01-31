package org.playuniverse.minecraft.wildcard.spigot;

import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.MessageAdapter;
import org.playuniverse.minecraft.wildcard.core.ServiceAdapter;
import org.playuniverse.minecraft.wildcard.core.message.PlatformComponentParser;
import org.playuniverse.minecraft.wildcard.core.settings.PluginSettings;

public final class SpigotService extends ServiceAdapter {
    
    private final PlatformComponentParser parser;

    public SpigotService(final PlatformComponentParser parser, final PluginSettings settings) {
        super(settings);
        this.parser = parser;
    }

    @Override
    protected MessageAdapter buildAdapter(final UUID uniqueId) {
        return new SpigotSender(parser, uniqueId);
    }

}
