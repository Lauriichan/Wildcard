package me.lauriichan.minecraft.wildcard.core.data.storage.sqlite.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.lauriichan.minecraft.wildcard.core.data.migration.type.SQLMigrationType;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;
import me.lauriichan.minecraft.wildcard.core.data.storage.sqlite.SQLiteDatabase;
import me.lauriichan.minecraft.wildcard.core.data.storage.sqlite.SQLiteMigration;
import me.lauriichan.minecraft.wildcard.core.data.storage.util.UUIDHelper;
import me.lauriichan.minecraft.wildcard.migration.Date;
import me.lauriichan.minecraft.wildcard.migration.Migration;

@Migration(source = SQLiteDatabase.class, type = SQLMigrationType.class)
public final class HistoryMigration2022_02_04_0_40 extends SQLiteMigration {

    public HistoryMigration2022_02_04_0_40() {
        super(SQLTable.HISTORY);
    }

    @Override
    public String getOldFormat() {
        return "User BINARY(16) NOT NULL, TokenOwner BINARY(16), Time VARCHAR(22) NOT NULL";
    }

    @Override
    public String getNewFormat() {
        return "User VARCHAR(36) NOT NULL, TokenOwner VARCHAR(36), Time VARCHAR(22) NOT NULL";
    }

    @Override
    public void migrateBatch(ResultSet legacyData, Connection connection, String table) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format(SQLiteDatabase.INSERT_HISTORY_ALLOW, table));
        while (legacyData.next()) {
            statement.setString(1, UUIDHelper.toUniqueId(legacyData.getBytes("User")).toString());
            byte[] owner = legacyData.getBytes("TokenOwner");
            statement.setString(2, owner == null ? null : UUIDHelper.toUniqueId(owner).toString());
            statement.setString(3, legacyData.getString("Time"));
            statement.addBatch();
        }
        statement.executeUpdate();
    }

    @Override
    protected long getDate() {
        return Date.of(0, 40, 4, 2, 2022);
    }

}
