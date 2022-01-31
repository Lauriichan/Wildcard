package org.playuniverse.minecraft.wildcard.core.listener;

import org.playuniverse.minecraft.wildcard.core.data.storage.Database;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public abstract class ConnectionListener {

    private final Container<Database> database;

    public ConnectionListener(final Container<Database> database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database.get();
    }

}
