package me.lauriichan.minecraft.wildcard.core.data.storage;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class HistoryEntry {

    private final UUID uniqueId;
    private final UUID tokenId;
    private final OffsetDateTime time;

    public HistoryEntry(final UUID uniqueId, final UUID tokenId, final OffsetDateTime time) {
        this.uniqueId = uniqueId;
        this.tokenId = tokenId;
        this.time = time;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UUID getTokenId() {
        return tokenId;
    }

    public boolean hasTokenId() {
        return tokenId != null;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public String getTimeAsString() {
        return Token.FORMATTER.format(time);
    }

}
