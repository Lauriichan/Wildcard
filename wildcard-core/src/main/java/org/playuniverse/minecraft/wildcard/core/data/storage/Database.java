package org.playuniverse.minecraft.wildcard.core.data.storage;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class Database {

    protected final Executor executor;

    public Database(final Executor executor) {
        this.executor = executor;
    }

    public abstract CompletableFuture<Boolean> hasToken(UUID uniqueId);

    public final CompletableFuture<Void> deleteToken(final Token token) {
        return deleteToken(token.getOwner(), token.getToken());
    }

    public abstract CompletableFuture<Void> deleteToken(UUID uniqueId, String tokenHash);

    public abstract CompletableFuture<Token> getToken(UUID uniqueId);

    public abstract CompletableFuture<Token> getTokenOrGenerate(UUID uniqueId, int uses, OffsetDateTime expires);

    public abstract CompletableFuture<Void> updateToken(Token token);

    public abstract CompletableFuture<RequestResult> deny(UUID uniqueId);

    public abstract CompletableFuture<RequestResult> allow(UUID uniqueId, UUID targetId);

    public abstract CompletableFuture<RequestResult> allow(UUID uniqueId, String token);

    public abstract CompletableFuture<Boolean> isAllowed(UUID uniqueId);

    public abstract CompletableFuture<HistoryEntry[]> getHistory(UUID uniqueId);

    public abstract void close();

}
