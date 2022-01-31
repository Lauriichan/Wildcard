package org.playuniverse.minecraft.wildcard.core.web.session;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

import org.playuniverse.minecraft.wildcard.core.data.container.nbt.NbtAdapterRegistry;
import org.playuniverse.minecraft.wildcard.core.data.container.nbt.NbtContainer;

public final class ClientSession {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;

    private final SessionManager sessionOwner;

    private final String id;
    private final OffsetDateTime time = OffsetDateTime.now().plusHours(3);

    private final NbtContainer container;
    
    private final AtomicBoolean used = new AtomicBoolean(false);

    public ClientSession(final SessionManager sessionOwner, final NbtAdapterRegistry registry, final String id) {
        this.sessionOwner = sessionOwner;
        this.container = new NbtContainer(registry);
        this.id = id;
    }

    public SessionManager getSessionOwner() {
        return sessionOwner;
    }

    public boolean hasExpired() {
        return time.isAfter(OffsetDateTime.now());
    }
    
    public boolean isUsed() {
        return used.get();
    }
    
    public void setUsed(boolean used) {
        this.used.set(used);
    }

    public NbtContainer getData() {
        return container;
    }

    public String getId() {
        return id;
    }

    public String getExpireTimeString() {
        return time.format(FORMATTER);
    }

}
