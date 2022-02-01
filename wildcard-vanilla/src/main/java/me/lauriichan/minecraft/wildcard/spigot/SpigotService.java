package me.lauriichan.minecraft.wildcard.spigot;

import java.util.UUID;

import me.lauriichan.minecraft.wildcard.core.MessageAdapter;
import me.lauriichan.minecraft.wildcard.core.ServiceAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponentParser;
import me.lauriichan.minecraft.wildcard.core.settings.PluginSettings;

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
