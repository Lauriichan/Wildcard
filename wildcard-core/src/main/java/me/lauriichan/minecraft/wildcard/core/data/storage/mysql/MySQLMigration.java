package me.lauriichan.minecraft.wildcard.core.data.storage.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.lauriichan.minecraft.wildcard.core.data.migration.impl.SQLMigration;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;

public abstract class MySQLMigration extends SQLMigration {

    private static final String TEST_EXISTENCE = "SHOW TABLES LIKE '%s'";
    private static final String SELECT_TABLE = "SHOW CREATE TABLE %s";
    private static final String LIMIT_FORMAT = "LIMIT %s, %s";

    public MySQLMigration(SQLTable table) {
        super(table);
    }

    @Override
    public ResultSet requestTableSql(String table, Connection connection) throws SQLException {
        ResultSet set = connection.prepareStatement(String.format(TEST_EXISTENCE, table)).executeQuery();
        if (!set.next()) {
            return set;
        }
        return connection.prepareStatement(String.format(SELECT_TABLE, table)).executeQuery();
    }

    @Override
    public String getFormat(ResultSet set) throws SQLException {
        return set.getString(2);
    }

    @Override
    public String getLimit(long offset, long limit) {
        return String.format(LIMIT_FORMAT, offset, limit);
    }

}
