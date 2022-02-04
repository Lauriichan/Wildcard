package me.lauriichan.minecraft.wildcard.core.data.migration.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;
import me.lauriichan.minecraft.wildcard.migration.MigrationProvider;

public abstract class SQLMigration extends MigrationProvider {

    private final SQLTable table;

    public SQLMigration(SQLTable table) {
        this.table = table;
    }

    public final SQLTable getTable() {
        return table;
    }

    public abstract String getOldFormat();

    public abstract String getNewFormat();

    public abstract String getFormat(ResultSet set) throws SQLException;

    public abstract ResultSet requestTableSql(String table, Connection connection) throws SQLException;

    public abstract void migrateBatch(ResultSet legacyData, Connection connection, String table) throws SQLException;

}
