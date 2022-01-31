package org.playuniverse.minecraft.wildcard.core.data.storage;

public final class DatabaseInitializationException extends RuntimeException {

    private static final long serialVersionUID = -7764756877418866620L;

    public DatabaseInitializationException(final String message) {
        super(message);
    }

    public DatabaseInitializationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
