package me.lauriichan.minecraft.wildcard.core.data.migration.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;

import me.lauriichan.minecraft.wildcard.core.Wildcard;
import me.lauriichan.minecraft.wildcard.core.data.migration.impl.SQLMigration;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLDatabase;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;
import me.lauriichan.minecraft.wildcard.core.util.Singleton;
import me.lauriichan.minecraft.wildcard.migration.IMigrationManager;
import me.lauriichan.minecraft.wildcard.migration.MigrationTarget;
import me.lauriichan.minecraft.wildcard.migration.MigrationType;

public final class SQLMigrationType extends MigrationType<SQLDatabase, SQLMigration> {

    private static final long ROW_LIMIT = 500;

    private static final String CREATE_TABLE = "CREATE TABLE %s(%s)";
    private static final String RENAME_TABLE = "ALTER TABLE %s RENAME TO %s";
    private static final String SELECT_TABLE = "SELECT * FROM %s ";
    private static final String DROP_TABLE = "DROP TABLE %s";

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
        loop:
        for (SQLTable table : SQLTable.values()) {
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
                State state = getFormatState(first, source.getTableName(table), first.getNewFormat(), connection, false);
                switch (state) {
                case LEGACY:
                    break;
                case NOT_AVAILABLE:
                    logger.log(LogTypeId.INFO, "Table '" + source.getTableName(table) + "' doesn't exist, creating...");
                    PreparedStatement statement = connection
                        .prepareStatement(String.format(CREATE_TABLE, source.getTableName(table), first.getNewFormat()));
                    statement.closeOnCompletion();
                    statement.executeUpdate();
                    logger.log(LogTypeId.INFO, "Table '" + source.getTableName(table) + "' was successfully created!");
                    continue loop;
                default: // We're up2date, no migration required
                    logger.log(LogTypeId.INFO, "Table '" + source.getTableName(table) + "' is up2date!");
                    continue loop;
                }
                logger.log(LogTypeId.WARNING, "Table '" + source.getTableName(table) + "' has an old format, migrating...");
                // We're not up2date so we check for the oldest version which is compatible
                boolean migrate = false;
                for (int i = migrations.size() - 1; i >= 0; i--) {
                    SQLMigration migration = migrations.get(i);
                    if (!migrate && getFormatState(migration, source.getTableName(table), migration.getNewFormat(), connection, true) == State.UP2DATE) {
                        migrate = true; // Now we know which version is the oldest and can start to migrate to newer ones
                        continue;
                    }
                    if (!migrate) {
                        if (i == 0) { // Force migrate oldest migration
                            i = migrations.size();
                            migrate = true;
                        }
                        continue;
                    }
                    if (!applyMigration(logger, connection, source, migration)) {
                        throw new IllegalStateException("Failed to migrate database '" + source.getClass().getSimpleName() + "'!");
                    }
                }
            } catch (SQLException e) {
                logger.log(LogTypeId.ERROR, "Failed to migrate table '" + source.getTableName(table) + "'");
                logger.log(LogTypeId.ERROR, e);
            }
        }
    }

    private boolean applyMigration(ILogger logger, Connection connection, SQLDatabase source, SQLMigration migration) throws SQLException {
        final String table = source.getTableName(migration.getTable());
        final String oldFormat = migration.getOldFormat();
        final String newFormat = migration.getNewFormat();
        final String tableLegacy = table + "_LEGACY";
        // Rename table
        PreparedStatement statement = connection.prepareStatement(String.format(RENAME_TABLE, table, tableLegacy));
        statement.execute();
        statement.close();
        // Create new table
        statement = connection.prepareStatement(String.format(CREATE_TABLE, table, newFormat));
        statement.executeUpdate();
        // Request table data
        final String selectLegacyTable = String.format(SELECT_TABLE, tableLegacy);
        try {
            logger.log(LogTypeId.INFO, "[" + migration.getId() + "] Migrated 0 entries of Table '" + table + "'...");
            long amount = 0;
            PreparedStatement batch = migration.startBatch(connection, table);
            while (true) {
                statement = connection.prepareStatement(selectLegacyTable + migration.getLimit(amount, ROW_LIMIT));
                statement.closeOnCompletion();
                ResultSet set = statement.executeQuery();
                int next = 0;
                batch.clearBatch();
                while (set.next()) {
                    migration.migrateBatch(batch, set);
                    next++;
                }
                if (!set.isClosed()) {
                    set.close();
                }
                if (next == 0) {
                    batch.close();
                    break;
                }
                batch.executeBatch();
                amount += next;
                logger.log(LogTypeId.INFO, "[" + migration.getId() + "] Migrated " + amount + " entries of Table '" + table + "'...");
            }
            logger.log(LogTypeId.INFO, "[" + migration.getId() + "] Migrated a total of " + amount + " entries of Table '" + table + "'!");
        } catch (SQLException exp) {
            logger.log(LogTypeId.ERROR,
                "Failed to migrate table '" + table + "' from (" + oldFormat + ") to (" + newFormat + ") [" + migration.getId() + "]");
            logger.log(LogTypeId.ERROR, exp);
            return false;
        }
        logger.log(LogTypeId.WARNING, "[" + migration.getId() + "] Migration of Table '" + table + "' was done successfully");
        logger.log(LogTypeId.WARNING, "[" + migration.getId() + "] Dropping old table '" + tableLegacy + "'!");
        try {
            statement = connection.prepareStatement(String.format(DROP_TABLE, tableLegacy));
            statement.execute();
            statement.close();
        } catch (SQLException exp) {
            logger.log(LogTypeId.WARNING, "[" + migration.getId() + "] Failed to drop old table '" + tableLegacy + "'!");
            if (Wildcard.isDebug()) {
                logger.log(LogTypeId.DEBUG, exp);
            }
            return true;
        }
        logger.log(LogTypeId.WARNING, "[" + migration.getId() + "] Old table '" + tableLegacy + "' dropped successfully!");
        return true;
    }

    private final State getFormatState(SQLMigration migration, String table, String format, Connection connection, boolean flag) throws SQLException {
        ResultSet set = migration.requestTableSql(table, connection);
        if (!set.next()) {
            return State.NOT_AVAILABLE;
        }
        String tableFormat = extractFormat(migration.getFormat(set));
        if (!set.isClosed()) {
            set.close();
        }
        if (tableFormat.equals(format) == flag) {
            return State.LEGACY;
        }
        return State.UP2DATE;
    }

    private String extractFormat(String input) {
        return (input = input.split("\\(", 2)[1]).substring(0, input.length() - 1).trim();
    }

    private static enum State {

        LEGACY,
        UP2DATE,
        NOT_AVAILABLE;

    }

}
