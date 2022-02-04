package me.lauriichan.minecraft.wildcard.core.data.migration.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;

import me.lauriichan.minecraft.wildcard.core.data.migration.impl.SQLMigration;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLDatabase;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;
import me.lauriichan.minecraft.wildcard.core.util.Singleton;
import me.lauriichan.minecraft.wildcard.migration.IMigrationManager;
import me.lauriichan.minecraft.wildcard.migration.MigrationTarget;
import me.lauriichan.minecraft.wildcard.migration.MigrationType;

public final class SQLMigrationType extends MigrationType<SQLDatabase, SQLMigration> {

    private static final String CREATE_TABLE = "CREATE TABLE %s(%s)";
    private static final String RENAME_TABLE = "ALTER TABLE %s RENAME TO %s";
    private static final String SELECT_TABLE = "SELECT * FROM %s LIMIT 100";

    private static final Function<SQLTable, ArrayList<SQLMigration>> BUILDER = (i) -> new ArrayList<>();

    public SQLMigrationType() {
        super(SQLDatabase.class, SQLMigration.class);
    }

    private final EnumMap<SQLTable, ArrayList<SQLMigration>> collect(IMigrationManager manager, Class<?> source) {
        EnumMap<SQLTable, ArrayList<SQLMigration>> migrations = new EnumMap<>(SQLTable.class);
        List<MigrationTarget<SQLMigration>> targets = getTargets(manager);
        if (targets.isEmpty()) {
            return migrations;
        }
        for (int i = 0; i < targets.size(); i++) {
            MigrationTarget<SQLMigration> target = targets.get(i);
            if (!target.getPoint().source().isAssignableFrom(source)) {
                continue;
            }
            SQLMigration migration = target.getMigration();
            migrations.computeIfAbsent(migration.getTable(), BUILDER).add(migration);
        }
        return migrations;
    }

    @Override
    public void migrate(IMigrationManager manager, SQLDatabase source) throws Exception {
        EnumMap<SQLTable, ArrayList<SQLMigration>> tableMigrations = collect(manager, source.getClass());
        if (tableMigrations.isEmpty()) {
            return;
        }
        ILogger logger = Singleton.get(ILogger.class);
        ArrayList<CompletableFuture<Void>> tasks = new ArrayList<>();
        for (SQLTable table : SQLTable.values()) {
            tasks.add(CompletableFuture.runAsync(() -> {
                if (!tableMigrations.containsKey(table)) {
                    return;
                }
                ArrayList<SQLMigration> migrations = tableMigrations.get(table);
                if (migrations.isEmpty()) {
                    return; // How?
                }
                Collections.sort(migrations);
                SQLMigration first = migrations.get(0);
                try (Connection connection = source.getConnection()) {
                    State state = getFormatState(first, source.getTableName(table), first.getOldFormat(), connection);
                    switch (state) {
                    case LEGACY:
                        break;
                    case NOT_AVAILABLE:
                        connection.prepareStatement(String.format(CREATE_TABLE, table, first.getNewFormat())).executeUpdate();
                    default: // We're up2date, no migration required
                        return;
                    }
                    // We're not up2date so we check for the oldest version which is compatbile
                    boolean migrate = false;
                    for (int i = migrations.size() - 1; i >= 0; i--) {
                        SQLMigration migration = migrations.get(i);
                        if (getFormatState(migration, source.getTableName(table), migration.getOldFormat(), connection) == State.UP2DATE) {
                            migrate = true; // Now we know which version is the oldest and can start to migrate to newer ones
                            continue;
                        }
                        if (!migrate) {
                            continue;
                        }
                        if (!applyMigration(logger, connection, source, migration)) {
                            break;
                        }
                    }
                } catch (SQLException e) {
                    logger.log(LogTypeId.ERROR, "Failed to migrate table '" + table + "'");
                    logger.log(LogTypeId.ERROR, e);
                }
            }));
        }
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).join(); // Await all tasks until completion
        }
    }

    private boolean applyMigration(ILogger logger, Connection connection, SQLDatabase source, SQLMigration migration) throws SQLException {
        final String table = source.getTableName(migration.getTable());
        final String oldFormat = migration.getOldFormat();
        final String newFormat = migration.getNewFormat();
        final String tableLegacy = table + "_LEGACY";
        logger.log(LogTypeId.WARNING, "Table '" + table + "' has an old format, migrating...");
        // Rename table
        PreparedStatement statement = connection.prepareStatement(String.format(RENAME_TABLE, table, tableLegacy));
        statement.executeUpdate();
        // Create new table
        statement = connection.prepareStatement(String.format(CREATE_TABLE, table, newFormat));
        statement.executeUpdate();
        // Request table data
        try {
            statement = connection.prepareStatement(SELECT_TABLE, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            while (true) {
                ResultSet set = statement.executeQuery();
                if (!set.next()) {
                    break;
                }
                set.beforeFirst();
                migration.migrateBatch(set, connection, table);
                set.beforeFirst();
                while (set.next()) {
                    set.deleteRow();
                }
            }
        } catch (SQLException exp) {
            logger.log(LogTypeId.ERROR,
                "Failed to migrate table '" + table + "' to from (" + oldFormat + ") to (" + newFormat + ") [" + migration.getId() + "]");
            logger.log(LogTypeId.ERROR, exp);
            return false;
        }
        logger.log(LogTypeId.WARNING, "Migration of Table '" + table + "' was done successfully");
        return true;
    }

    private final State getFormatState(SQLMigration migration, String table, String oldFormat, Connection connection) throws SQLException {
        ResultSet set = migration.requestTableSql(table, connection);
        if (!set.next()) {
            return State.NOT_AVAILABLE;
        }
        String format = extractFormat(migration.getFormat(set));
        if (format.equals(oldFormat)) {
            return State.LEGACY;
        }
        return State.UP2DATE;
    }

    private String extractFormat(String input) {
        return (input = input.split("(", 2)[1]).substring(0, input.length() - 1);
    }

    private static enum State {

        LEGACY,
        UP2DATE,
        NOT_AVAILABLE;

    }

}
