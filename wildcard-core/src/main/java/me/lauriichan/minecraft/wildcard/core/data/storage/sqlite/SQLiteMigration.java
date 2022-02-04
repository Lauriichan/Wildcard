package me.lauriichan.minecraft.wildcard.core.data.storage.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.lauriichan.minecraft.wildcard.core.data.migration.impl.SQLMigration;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;

public abstract class SQLiteMigration extends SQLMigration {

    private static final String SELECT_TABLE = "SELECT * FROM sqlite_master WHERE \"type\" = \"table\" AND name = ?";

    public SQLiteMigration(SQLTable table) {
        super(table);
    }

    @Override
    public ResultSet requestTableSql(String table, Connection connection) throws SQLException {
        return connection.prepareStatement(String.format(SELECT_TABLE, table)).executeQuery();
    }

    @Override
    public String getFormat(ResultSet set) throws SQLException {
        return set.getString("sql");
    }

}
