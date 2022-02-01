package me.lauriichan.minecraft.wildcard.core.listener;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.data.storage.Database;

public abstract class ConnectionListener {

    private final Container<Database> database;

    public ConnectionListener(final Container<Database> database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database.get();
    }

}
