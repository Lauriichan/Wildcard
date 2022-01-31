package me.lauriichan.minecraft.wildcard.sponge;

import java.util.UUID;

import me.lauriichan.minecraft.wildcard.core.MessageAdapter;
import me.lauriichan.minecraft.wildcard.core.ServiceAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponentParser;
import me.lauriichan.minecraft.wildcard.core.settings.PluginSettings;

public final class SpongeService extends ServiceAdapter {

    private final PlatformComponentParser parser;

    public SpongeService(final PlatformComponentParser parser, final PluginSettings settings) {
        super(settings);
        this.parser = parser;
    }

    @Override
    protected MessageAdapter buildAdapter(UUID uniqueId) {
        return new SpongeSender(parser, uniqueId);
    }

}
