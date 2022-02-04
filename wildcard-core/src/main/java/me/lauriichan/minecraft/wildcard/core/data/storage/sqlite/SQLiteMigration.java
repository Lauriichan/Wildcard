package me.lauriichan.minecraft.wildcard.core.data.storage.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.lauriichan.minecraft.wildcard.core.data.migration.impl.SQLMigration;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;

public abstract class SQLiteMigration extends SQLMigration {

    private static final String SELECT_TABLE = "SELECT * FROM sqlite_master WHERE \"type\" = \"table\" AND name = ?";
    private static final String LIMIT_FORMAT = "LIMIT %s OFFSET %s";

    public SQLiteMigration(SQLTable table) {
        super(table);
    }

    @Override
    public ResultSet requestTableSql(String table, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT_TABLE);
        statement.setString(1, table);
        return statement.executeQuery();
    }

    @Override
    public String getFormat(ResultSet set) throws SQLException {
        return set.getString("sql");
    }
    
    @Override
    public String getLimit(long offset, long limit) {
        return String.format(LIMIT_FORMAT, limit, offset);
    }

}
