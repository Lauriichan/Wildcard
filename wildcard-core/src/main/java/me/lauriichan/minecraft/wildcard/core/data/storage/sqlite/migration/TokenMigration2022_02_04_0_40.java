package me.lauriichan.minecraft.wildcard.core.data.storage.sqlite.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Hex;

import me.lauriichan.minecraft.wildcard.core.data.migration.type.SQLMigrationType;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;
import me.lauriichan.minecraft.wildcard.core.data.storage.sqlite.SQLiteDatabase;
import me.lauriichan.minecraft.wildcard.core.data.storage.sqlite.SQLiteMigration;
import me.lauriichan.minecraft.wildcard.core.data.storage.util.UUIDHelper;
import me.lauriichan.minecraft.wildcard.migration.Date;
import me.lauriichan.minecraft.wildcard.migration.Migration;

@Migration(source = SQLiteDatabase.class, type = SQLMigrationType.class)
public final class TokenMigration2022_02_04_0_40 extends SQLiteMigration {

    public TokenMigration2022_02_04_0_40() {
        super(SQLTable.TOKEN);
    }

    @Override
    public String getOldFormat() {
        return "Owner BINARY(16) NOT NULL, Token BINARY(20) NOT NULL, Uses INT NOT NULL, Expires VARCHAR(22), CONSTRAINT UToken UNIQUE (Token), CONSTRAINT POwner PRIMARY KEY (Owner)";
    }

    @Override
    public String getNewFormat() {
        return "Owner VARCHAR(36) NOT NULL, Token VARCHAR(40) NOT NULL, Uses INT NOT NULL, Expires VARCHAR(22), CONSTRAINT UToken UNIQUE (Token), CONSTRAINT POwner PRIMARY KEY (Owner)";
    }

    @Override
    public PreparedStatement startBatch(Connection connection, String table) throws SQLException {
        return connection.prepareStatement(String.format(SQLiteDatabase.INSERT_TOKEN, table));
    }

    @Override
    public void migrateBatch(PreparedStatement statement, ResultSet legacyData) throws SQLException {
        statement.setString(1, UUIDHelper.toUniqueId(legacyData.getBytes("Owner")).toString());
        statement.setString(2, Hex.encodeHexString(legacyData.getBytes("Token")));
        statement.setString(3, legacyData.getString("Expires"));
        statement.addBatch();
    }

    @Override
    protected long getDate() {
        return Date.of(0, 40, 4, 2, 2022);
    }

}
