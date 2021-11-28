package org.playuniverse.minecraft.wildcard.core;

import java.util.UUID;

import org.playuniverse.minecraft.wildcard.core.settings.PluginSettings;
import org.playuniverse.minecraft.wildcard.core.util.ComponentParser;
import org.playuniverse.minecraft.wildcard.core.util.MojangProfileService;
import org.playuniverse.minecraft.wildcard.core.util.cache.ThreadSafeCache;
import org.playuniverse.minecraft.wildcard.core.util.tick.ITickReceiver;

public abstract class ServiceAdapter implements ITickReceiver {

    protected final ComponentParser parser;
    protected final ThreadSafeCache<UUID, MessageAdapter> adapterCache;
    protected final ThreadSafeCache<String, UUID> uuidCache;
    protected final ThreadSafeCache<UUID, String> nameCache;

    public ServiceAdapter(final ComponentParser parser, final PluginSettings settings) {
        this.parser = parser;
        this.adapterCache = new ThreadSafeCache<>(UUID.class, Math.max(60, Math.abs(settings.getInteger("cache.message.time", 900))));
        this.uuidCache = new ThreadSafeCache<>(String.class, Math.max(60, Math.abs(settings.getInteger("cache.uuid.time", 600))));
        this.nameCache = new ThreadSafeCache<>(UUID.class, Math.max(60, Math.abs(settings.getInteger("cache.name.time", 300))));
    }

    public final MessageAdapter getMessageAdapter(final UUID uniqueId) {
        if (uniqueId == null) {
            return null;
        }
        if (adapterCache.has(uniqueId)) {
            return adapterCache.get(uniqueId);
        }
        final MessageAdapter adapter = buildAdapter(uniqueId);
        adapterCache.put(uniqueId, adapter);
        return adapter;
    }

    protected abstract MessageAdapter buildAdapter(UUID uniqueId);

    public final UUID getUniqueId(final String name) {
        if (uuidCache.has(name)) {
            return uuidCache.get(name);
        }
        final UUID uniqueId = MojangProfileService.getUniqueId(name);
        if (uniqueId == null) {
            return null;
        }
        uuidCache.put(name, uniqueId);
        return uniqueId;
    }

    public final String getName(final UUID uniqueId) {
        if (nameCache.has(uniqueId)) {
            return nameCache.get(uniqueId);
        }
        final String name = MojangProfileService.getName(uniqueId);
        if (name == null || name.isBlank()) {
            return null;
        }
        nameCache.put(uniqueId, name);
        return name;
    }

    @Override
    public void onTick(final long deltaTime) {
        adapterCache.tick();
        uuidCache.tick();
        nameCache.tick();
    }

}
