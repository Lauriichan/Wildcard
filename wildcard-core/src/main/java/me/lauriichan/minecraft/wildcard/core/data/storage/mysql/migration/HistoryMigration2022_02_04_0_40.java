package me.lauriichan.minecraft.wildcard.core.data.storage.mysql.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import me.lauriichan.minecraft.wildcard.core.data.migration.type.SQLMigrationType;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;
import me.lauriichan.minecraft.wildcard.core.data.storage.mysql.MySQLDatabase;
import me.lauriichan.minecraft.wildcard.core.data.storage.mysql.MySQLMigration;
import me.lauriichan.minecraft.wildcard.core.data.storage.util.UUIDHelper;
import me.lauriichan.minecraft.wildcard.migration.Date;
import me.lauriichan.minecraft.wildcard.migration.Migration;

@Migration(source = MySQLDatabase.class, type = SQLMigrationType.class)
public final class HistoryMigration2022_02_04_0_40 extends MySQLMigration {

    public HistoryMigration2022_02_04_0_40() {
        super(SQLTable.HISTORY);
    }

    @Override
    public String getOldFormat() {
        return "User BINARY(16) NOT NULL, TokenOwner BINARY(16), Time DATETIME NOT NULL";
    }

    @Override
    public String getNewFormat() {
        return "User VARCHAR(36) NOT NULL, TokenOwner VARCHAR(36), Time DATETIME NOT NULL";
    }

    @Override
    public PreparedStatement startBatch(Connection connection, String table) throws SQLException {
        return connection.prepareStatement(String.format(MySQLDatabase.INSERT_HISTORY_ALLOW, table));
    }

    @Override
    public void migrateBatch(PreparedStatement statement, ResultSet legacyData) throws SQLException {
        statement.setString(1, UUIDHelper.toUniqueId(legacyData.getBytes("User")).toString());
        byte[] owner = legacyData.getBytes("TokenOwner");
        statement.setString(2, owner == null ? null : UUIDHelper.toUniqueId(owner).toString());
        statement.setObject(3, legacyData.getObject("Time", OffsetDateTime.class));
        statement.addBatch();
    }

    @Override
    protected long getDate() {
        return Date.of(0, 40, 4, 2, 2022);
    }

}
