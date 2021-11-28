package org.playuniverse.minecraft.wildcard.bungee;

import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.MessageAdapter;
import org.playuniverse.minecraft.wildcard.core.ServiceAdapter;
import org.playuniverse.minecraft.wildcard.core.settings.PluginSettings;
import org.playuniverse.minecraft.wildcard.core.util.ComponentParser;

public final class BungeeService extends ServiceAdapter {

    public BungeeService(final ComponentParser parser, final PluginSettings settings) {
        super(parser, settings);
    }

    @Override
    protected MessageAdapter buildAdapter(final UUID uniqueId) {
        return new BungeeSender(parser, uniqueId);
    }

}
