package me.lauriichan.minecraft.wildcard.core.data.storage.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

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
        String string = set.getString(2);
        String[] lines = string.split("\n");
        for (int index = 1; index < lines.length; index++) {
            String line = lines[index].trim();
            if (line.startsWith("`")) {
                line = line.replace("`", "");
                String[] parts = line.split(" ");
                parts[1] = parts[1].toUpperCase();
                if (!(parts[1].startsWith("VARCHAR") || parts[1].startsWith("BINARY"))) {
                    parts[1] = parts[1].split("\\(")[0];
                }
                lines[index] = Arrays.stream(parts).collect(Collectors.joining(" ")).replace(" DEFAULT NULL", "");
                continue;
            }
            if (line.startsWith(")")) {
                lines[index] = "";
                continue;
            }
            lines[index] = line.replace("`", "");
        }
        return Arrays.stream(lines).collect(Collectors.joining(" "));
    }

    @Override
    public String getLimit(long offset, long limit) {
        return String.format(LIMIT_FORMAT, offset, limit);
    }

}
