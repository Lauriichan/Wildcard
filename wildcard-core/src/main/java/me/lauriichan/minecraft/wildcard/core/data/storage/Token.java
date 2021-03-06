package me.lauriichan.minecraft.wildcard.core.data.storage;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public final class Token {

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    private final ReentrantLock lock = new ReentrantLock();

    private final UUID owner;
    private final String token;
    private final OffsetDateTime expires;

    private int uses;

    public Token(final UUID owner, final String token, final int uses, final OffsetDateTime expires) {
        this.expires = expires;
        this.token = Objects.requireNonNull(token);
        this.owner = Objects.requireNonNull(owner);
        this.uses = uses;
    }

    public boolean isExpired() {
        return uses == 0 || (expires == null ? false : OffsetDateTime.now().isAfter(expires));
    }

    public OffsetDateTime getExpires() {
        return expires;
    }

    public String getExpiresAsString() {
        return expires == null ? "N/A" : FORMATTER.format(expires);
    }

    public UUID getOwner() {
        return owner;
    }

    public String getToken() {
        return token;
    }

    public int getUses() {
        return uses;
    }

    public int use() {
        lock.lock();
        try {
            if(isExpired()) {
                return -1;
            }
            uses -= 1;
            return uses;
        } finally {
            lock.unlock();
        }
    }

}
