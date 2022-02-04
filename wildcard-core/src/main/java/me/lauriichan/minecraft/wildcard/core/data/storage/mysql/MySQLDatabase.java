package me.lauriichan.minecraft.wildcard.core.data.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.mysql.cj.jdbc.Driver;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.random.Keys;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;

import me.lauriichan.minecraft.wildcard.core.data.migration.MigrationManager;
import me.lauriichan.minecraft.wildcard.core.data.migration.type.SQLMigrationType;
import me.lauriichan.minecraft.wildcard.core.data.storage.DatabaseInitializationException;
import me.lauriichan.minecraft.wildcard.core.data.storage.HistoryEntry;
import me.lauriichan.minecraft.wildcard.core.data.storage.RequestResult;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLDatabase;
import me.lauriichan.minecraft.wildcard.core.data.storage.SQLTable;
import me.lauriichan.minecraft.wildcard.core.data.storage.Token;
import me.lauriichan.minecraft.wildcard.core.data.storage.util.UUIDHelper;
import me.lauriichan.minecraft.wildcard.core.settings.DatabaseSettings;
import me.lauriichan.minecraft.wildcard.core.settings.PluginSettings;
import me.lauriichan.minecraft.wildcard.core.util.cache.Cache;
import me.lauriichan.minecraft.wildcard.core.util.cache.ThreadSafeCache;
import me.lauriichan.minecraft.wildcard.core.util.tick.ITickReceiver;
import me.lauriichan.minecraft.wildcard.core.util.tick.TickTimer;

public class MySQLDatabase extends SQLDatabase implements ITickReceiver {

    public static final String SELECT_TOKEN_BY_USER = "SELECT * FROM %s WHERE Owner = ?";
    public static final String SELECT_TOKEN_BY_TOKEN = "SELECT * FROM %s WHERE Token = ?";
    public static final String UPDATE_TOKEN = "UPDATE %s SET Uses = ? WHERE Token = ?";
    public static final String INSERT_TOKEN = "INSERT INTO %s VALUES (?, ?, ?, ?)";
    public static final String DELETE_TOKEN = "DELETE FROM %s WHERE Owner = ? AND Token = ?";

    public static final String INSERT_HISTORY_DENY = "INSERT INTO %s(User, Time) VALUES (?, ?)";
    public static final String INSERT_HISTORY_ALLOW = "INSERT INTO %s VALUES (?, ?, ?)";
    public static final String SELECT_HISTORY_BY_USER = "SELECT * FROM %s WHERE User = ? ORDER BY Time DESC";
    public static final String SELECT_LATEST_HISTORY_ENTRY_BY_USER = "SELECT * FROM %s WHERE User = ? ORDER BY Time DESC LIMIT 1";

    public static final short DEFAULT_PORT = 3306;

    private final TickTimer cacheTimer;

    private final String tokenTable;
    private final String historyTable;
    private final HikariPool pool;

    private final ILogger logger;

    private final Cache<UUID, HistoryEntry[]> historyCache;
    private final Cache<UUID, Boolean> wildcardCache;
    private final Cache<UUID, Token> tokenCache;

    private final String selectTokenByUser;
    private final String selectTokenByToken;
    private final String updateToken;
    private final String insertToken;
    private final String deleteToken;

    private final String insertHistoryDeny;
    private final String insertHistoryAllow;
    private final String selectHistoryByUser;
    private final String selectLatestHistoryEntryByUser;

    private final Keys tokenGen = new Keys(System.currentTimeMillis());

    public MySQLDatabase(final ILogger logger, final Executor executor, final TickTimer cacheTimer, final PluginSettings pluginSettings,
        final DatabaseSettings settings) {
        super(executor);
        this.cacheTimer = cacheTimer;
        this.logger = logger;
        cacheTimer.add(this);
        this.wildcardCache = new ThreadSafeCache<>(UUID.class,
            Math.max(120, Math.abs(pluginSettings.getInteger("cache.wildcard.time", 1800))));
        this.tokenCache = new ThreadSafeCache<>(UUID.class, Math.max(600, Math.abs(pluginSettings.getInteger("cache.token.time", 3600))));
        this.historyCache = new ThreadSafeCache<>(UUID.class, Math.max(60, Math.abs(pluginSettings.getInteger("cache.history.time", 300))));
        this.tokenTable = settings.getString("mysql.table.token", "WildcardTokens");
        this.historyTable = settings.getString("mysql.table.history", "WildcardHistory");
        final HikariConfig config = new HikariConfig();
        config.setConnectionTimeout(Math.max(1000, Math.abs(settings.getInteger("mysql.timeout.connection", 7500))));
        config.setMaximumPoolSize(Math.max(1, Math.min(16, settings.getInteger("mysql.pool.max", 8))));
        config.setMinimumIdle(Math.max(1, Math.min(16, settings.getInteger("mysql.pool.min", 1))));
        config.setPoolName("Wildcard");
        config.setJdbcUrl("jdbc:mysql://" + settings.getString("mysql.host", "localhost") + ":"
            + Math.abs(settings.getShort("mysql.port", DEFAULT_PORT)) + "/" + settings.getString("mysql.database", "Wildcard"));
        config.setUsername(settings.getString("mysql.username", "root"));
        config.setPassword(settings.getString("mysql.password", "password"));
        config.setDriverClassName(Driver.class.getName());
        this.pool = new HikariPool(config);
        try (Connection connection = pool.getConnection(15000)) {
            final PreparedStatement statement = connection.prepareStatement("/* ping */ SELECT 1");
            statement.setQueryTimeout(15);
            statement.executeQuery();
        } catch (final SQLException exp) {
            close();
            throw new DatabaseInitializationException("MySQL connection test failed", exp);
        }
        try {
            MigrationManager.migrate(this, SQLMigrationType.class);
        } catch (final Exception exp) {
            close();
            throw new DatabaseInitializationException("MySQL database setup failed", exp);
        }
        this.selectTokenByToken = String.format(SELECT_TOKEN_BY_TOKEN, tokenTable);
        this.selectTokenByUser = String.format(SELECT_TOKEN_BY_USER, tokenTable);
        this.updateToken = String.format(UPDATE_TOKEN, tokenTable);
        this.insertToken = String.format(INSERT_TOKEN, tokenTable);
        this.deleteToken = String.format(DELETE_TOKEN, tokenTable);
        this.insertHistoryDeny = String.format(INSERT_HISTORY_DENY, historyTable);
        this.insertHistoryAllow = String.format(INSERT_HISTORY_ALLOW, historyTable);
        this.selectHistoryByUser = String.format(SELECT_HISTORY_BY_USER, historyTable);
        this.selectLatestHistoryEntryByUser = String.format(SELECT_LATEST_HISTORY_ENTRY_BY_USER, historyTable);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }
    
    @Override
    public String getTableName(SQLTable table) {
        switch(table) {
        case HISTORY:
            return historyTable;
        case TOKEN:
            return tokenTable;
        default:
            return null;
        }
    }

    @Override
    public void close() {
        try {
            pool.shutdown();
        } catch (final InterruptedException exp) {
            logger.log(LogTypeId.WARNING, "Something interrupted the Thread while trying to close HikariPool!");
            logger.log(LogTypeId.WARNING, exp);
        }
        cacheTimer.remove(this);
    }

    @Override
    public void onTick(final long deltaTime) {
        wildcardCache.tick();
        tokenCache.tick();
    }

    @Override
    public CompletableFuture<Boolean> isAllowed(final UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            if (wildcardCache.has(uniqueId)) {
                return wildcardCache.get(uniqueId);
            }
            try (Connection connection = pool.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(selectLatestHistoryEntryByUser);
                statement.setString(1, uniqueId.toString());
                final ResultSet set = statement.executeQuery();
                if (set.next()) {
                    final boolean state = set.getString("TokenOwner") != null;
                    wildcardCache.put(uniqueId, state);
                    return state;
                }
            } catch (final SQLException exp) {
                logger.log(LogTypeId.WARNING, "Failed to retrieve wildcard information of '" + uniqueId.toString() + "' from MySQL");
            }
            return false;
        }, executor);
    }

    @Override
    public CompletableFuture<Boolean> hasToken(final UUID uniqueId) {
        return getToken(uniqueId).thenComposeAsync(token -> CompletableFuture.completedFuture(token != null), executor);
    }

    @Override
    public CompletableFuture<Token> getToken(final UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            if (tokenCache.has(uniqueId)) {
                return tokenCache.get(uniqueId);
            }
            try (Connection connection = pool.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(selectTokenByUser);
                statement.setString(1, uniqueId.toString());
                final ResultSet set = statement.executeQuery();
                if (set.next()) {
                    final OffsetDateTime time = set.getObject("Time", OffsetDateTime.class);
                    final String tokenHash = set.getString("Token");
                    final UUID owner = UUIDHelper.fromString(set.getString("Owner"));
                    final int uses = set.getInt("Uses");
                    final Token token = new Token(owner, tokenHash, uses, time);
                    if (token.isExpired()) {
                        deleteToken(owner, tokenHash);
                        return null;
                    }
                    tokenCache.put(uniqueId, token);
                    return token;
                }
            } catch (final SQLException exp) {
                logger.log(LogTypeId.WARNING, "Failed to retrieve token information of '" + uniqueId.toString() + "' from MySQL");
            }
            return null;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteToken(final UUID uniqueId, final String tokenHash) {
        return CompletableFuture.runAsync(() -> {
            if (tokenCache.has(uniqueId)) {
                tokenCache.remove(uniqueId);
            }
            try (Connection connection = pool.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(deleteToken);
                statement.setString(1, uniqueId.toString());
                statement.setString(2, tokenHash);
                statement.executeUpdate();
            } catch (SQLException exp) {
                logger.log(LogTypeId.WARNING,
                    "Failed to delete token information of '" + tokenHash + "' owned by '" + uniqueId.toString() + "' from MySQL");
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Token> getTokenOrGenerate(final UUID uniqueId, final int uses, final OffsetDateTime expires) {
        return getToken(uniqueId)
            .thenComposeAsync(token -> token != null ? CompletableFuture.completedFuture(token) : CompletableFuture.supplyAsync(() -> {
                byte[] tokenRaw = DigestUtils.sha1(tokenGen.makeKey(12));
                try (Connection connection = pool.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(selectTokenByToken);
                    String tokenHash;
                    statement.setString(1, tokenHash = Hex.encodeHexString(tokenRaw));
                    while (statement.executeQuery().next()) {
                        tokenRaw = DigestUtils.sha1(tokenGen.makeKey(12));
                        statement.setString(1, tokenHash = Hex.encodeHexString(tokenRaw));
                    }
                    statement = connection.prepareStatement(insertToken);
                    statement.setString(1, uniqueId.toString());
                    statement.setString(2, tokenHash);
                    statement.setInt(3, uses);
                    statement.setObject(4, expires);
                    statement.executeUpdate();
                    final Token generatedToken = new Token(uniqueId, Hex.encodeHexString(tokenRaw), uses, expires);
                    tokenCache.put(uniqueId, token);
                    return generatedToken;
                } catch (final SQLException exp) {
                    logger.log(LogTypeId.WARNING, "Failed to store token created for '" + uniqueId.toString() + "' to MySQL");
                }
                return null;
            }, executor), executor);
    }

    @Override
    public CompletableFuture<Void> updateToken(final Token token) {
        return CompletableFuture.runAsync(() -> {
            if (token.getUses() == 0) {
                deleteToken(token).join();
                return;
            }
            try (Connection connection = pool.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(updateToken);
                statement.setInt(1, token.getUses());
                statement.setString(2, token.getToken());
                statement.executeUpdate();
            } catch (SQLException exp) {
                logger.log(LogTypeId.WARNING,
                    "Failed to send update of Token '" + token.getToken() + "' of '" + token.getOwner().toString() + "' to MySQL");
            }
        }, executor);
    }

    @Override
    public CompletableFuture<RequestResult> deny(final UUID uniqueId) {
        return isAllowed(uniqueId).thenComposeAsync(state -> {
            if (!state) {
                return CompletableFuture.completedFuture(RequestResult.KNOWN);
            }
            try (Connection connection = pool.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(insertHistoryDeny);
                statement.setString(1, uniqueId.toString());
                statement.setObject(2, OffsetDateTime.now());
                statement.executeUpdate();
                wildcardCache.put(uniqueId, false);
                return CompletableFuture.completedFuture(RequestResult.SUCCESS);
            } catch (final SQLException exp) {
                logger.log(LogTypeId.WARNING, "Failed to insert history (deny) of '" + uniqueId.toString() + "' to MySQL");
            }
            return CompletableFuture.completedFuture(RequestResult.FAILED);
        }, executor);
    }

    @Override
    public CompletableFuture<RequestResult> allow(final UUID uniqueId, final UUID targetId) {
        return isAllowed(uniqueId).thenComposeAsync(state -> {
            if (state) {
                return CompletableFuture.completedFuture(RequestResult.KNOWN);
            }
            try (Connection connection = pool.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(insertHistoryAllow);
                statement.setString(1, uniqueId.toString());
                statement.setString(2, targetId.toString());
                statement.setObject(3, OffsetDateTime.now());
                statement.executeUpdate();
                wildcardCache.put(uniqueId, true);
                return CompletableFuture.completedFuture(RequestResult.SUCCESS);
            } catch (final SQLException exp) {
                logger.log(LogTypeId.WARNING, "Failed to insert history (allow) of '" + uniqueId.toString() + "' to SQLite");
            }
            return CompletableFuture.completedFuture(RequestResult.FAILED);
        }, executor);
    }

    @Override
    public CompletableFuture<RequestResult> allow(final UUID uniqueId, final String tokenHash) {
        return isAllowed(uniqueId).thenComposeAsync(state -> {
            if (state) {
                return CompletableFuture.completedFuture(RequestResult.KNOWN);
            }
            try (Connection connection = pool.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(selectTokenByToken);
                statement.setString(1, tokenHash);
                final ResultSet set = statement.executeQuery();
                if (!set.next()) {
                    return CompletableFuture.completedFuture(RequestResult.FAILED);
                }
                final UUID targetId = UUIDHelper.fromString(set.getString("Owner"));
                final Token token = tokenCache.has(uniqueId) ? tokenCache.get(uniqueId)
                    : new Token(targetId, tokenHash, set.getInt("Uses"), set.getObject("Time", OffsetDateTime.class));
                if (token.use() == -1) {
                    deleteToken(token);
                    return CompletableFuture.completedFuture(RequestResult.FAILED);
                }
                updateToken(token);
                statement = connection.prepareStatement(insertHistoryAllow);
                statement.setString(1, uniqueId.toString());
                statement.setString(2, targetId.toString());
                statement.setObject(3, OffsetDateTime.now());
                statement.executeUpdate();
                wildcardCache.put(uniqueId, true);
                return CompletableFuture.completedFuture(RequestResult.SUCCESS);
            } catch (SQLException exp) {
                logger.log(LogTypeId.WARNING, "Failed to insert history (allow) of '" + uniqueId.toString() + "' to MySQL");
            }
            return CompletableFuture.completedFuture(RequestResult.FAILED);
        }, executor);
    }

    @Override
    public CompletableFuture<HistoryEntry[]> getHistory(final UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            if (historyCache.has(uniqueId)) {
                return historyCache.get(uniqueId);
            }
            final ArrayList<HistoryEntry> list = new ArrayList<>();
            try (Connection connection = pool.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(selectHistoryByUser);
                statement.setString(1, uniqueId.toString());
                final ResultSet set = statement.executeQuery();
                while (set.next()) {
                    final OffsetDateTime time = set.getObject("Time", OffsetDateTime.class);
                    final String target = set.getString("TokenOwner");
                    list.add(new HistoryEntry(uniqueId, target == null ? null : UUIDHelper.fromString(target), time));
                }
            } catch (final SQLException exp) {
                logger.log(LogTypeId.WARNING, "Failed to retrieve history of '" + uniqueId.toString() + "' from MySQL");
            }
            final HistoryEntry[] entries = list.toArray(new HistoryEntry[list.size()]);
            if (entries.length != 0) {
                historyCache.put(uniqueId, entries);
            }
            return entries;
        }, executor);
    }

}
