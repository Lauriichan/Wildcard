package me.lauriichan.minecraft.wildcard.forge;

import java.util.UUID;

import me.lauriichan.minecraft.wildcard.core.MessageAdapter;
import me.lauriichan.minecraft.wildcard.core.ServiceAdapter;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponentParser;
import me.lauriichan.minecraft.wildcard.core.settings.PluginSettings;

public final class ForgeService extends ServiceAdapter {

    private final PlatformComponentParser parser;

    public ForgeService(final PlatformComponentParser parser, final PluginSettings settings) {
        super(settings);
        this.parser = parser;
    }

    @Override
    protected MessageAdapter buildAdapter(UUID uniqueId) {
        return new ForgeSender(parser, uniqueId);
    }

}
