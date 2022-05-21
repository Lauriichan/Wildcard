package me.lauriichan.minecraft.wildcard.core.data.storage.mysql.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import org.apache.commons.codec.binary.Hex;

import me.lauriichan.minecraft.wildcard.core.data.migration.type.SQLMigrationType;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;
import me.lauriichan.minecraft.wildcard.core.data.storage.mysql.MySQLDatabase;
import me.lauriichan.minecraft.wildcard.core.data.storage.mysql.MySQLMigration;
import me.lauriichan.minecraft.wildcard.core.data.storage.util.TimeHelper;
import me.lauriichan.minecraft.wildcard.core.data.storage.util.UUIDHelper;
import me.lauriichan.minecraft.wildcard.migration.Date;
import me.lauriichan.minecraft.wildcard.migration.Migration;

@Migration(source = MySQLDatabase.class, type = SQLMigrationType.class)
public final class TokenMigration2022_05_21_13_23 extends MySQLMigration {

    public TokenMigration2022_05_21_13_23() {
        super(SQLTable.TOKEN);
    }

    @Override
    public String getOldFormat() {
        return "Owner VARCHAR(36) NOT NULL, Token VARCHAR(40) NOT NULL, Uses INT NOT NULL, Expires DATETIME, CONSTRAINT UToken UNIQUE (Token), CONSTRAINT POwner PRIMARY KEY (Owner)";
    }

    @Override
    public String getNewFormat() {
        return "Owner VARCHAR(36) NOT NULL, Token VARCHAR(40) NOT NULL, Uses INT NOT NULL, Expires VARCHAR(22), CONSTRAINT UToken UNIQUE (Token), CONSTRAINT POwner PRIMARY KEY (Owner)";
    }

    @Override
    public PreparedStatement startBatch(Connection connection, String table) throws SQLException {
        return connection.prepareStatement(String.format(MySQLDatabase.INSERT_TOKEN, table));
    }

    @Override
    public void migrateBatch(PreparedStatement statement, ResultSet legacyData) throws SQLException {
        statement.setString(1, UUIDHelper.toUniqueId(legacyData.getBytes("Owner")).toString());
        statement.setString(2, Hex.encodeHexString(legacyData.getBytes("Token")));
        statement.setInt(3, legacyData.getInt("Uses"));
        statement.setString(4, TimeHelper.toString(legacyData.getObject("Expires", OffsetDateTime.class)));
        statement.addBatch();
    }

    @Override
    protected long getDate() {
        return Date.of(13, 23, 21, 5, 2022);
    }

}
