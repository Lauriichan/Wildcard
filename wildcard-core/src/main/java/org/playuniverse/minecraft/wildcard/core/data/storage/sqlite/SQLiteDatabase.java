package org.playuniverse.minecraft.wildcard.core.data.storage.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.playuniverse.minecraft.wildcard.core.data.storage.Database;
import org.playuniverse.minecraft.wildcard.core.data.storage.DatabaseInitializationException;
import org.playuniverse.minecraft.wildcard.core.data.storage.HistoryEntry;
import org.playuniverse.minecraft.wildcard.core.data.storage.RequestResult;
import org.playuniverse.minecraft.wildcard.core.data.storage.Token;
import org.playuniverse.minecraft.wildcard.core.data.storage.util.TimeHelper;
import org.playuniverse.minecraft.wildcard.core.data.storage.util.UUIDHelper;
import org.playuniverse.minecraft.wildcard.core.settings.DatabaseSettings;
import org.playuniverse.minecraft.wildcard.core.settings.PluginSettings;
import org.playuniverse.minecraft.wildcard.core.util.cache.Cache;
import org.playuniverse.minecraft.wildcard.core.util.cache.ThreadSafeCache;
import org.playuniverse.minecraft.wildcard.core.util.tick.ITickReceiver;
import org.playuniverse.minecraft.wildcard.core.util.tick.TickTimer;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.random.Keys;
import com.syntaxphoenix.syntaxapi.utils.java.Files;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;

public class SQLiteDatabase extends Database implements ITickReceiver {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s%s";

    private static final String SELECT_TOKEN_BY_USER = "SELECT * FROM %s WHERE Owner = ?";
    private static final String SELECT_TOKEN_BY_TOKEN = "SELECT * FROM %s WHERE Token = ?";
    private static final String UPDATE_TOKEN = "UPDATE %s SET Uses = ? WHERE Token = ?";
    private static final String INSERT_TOKEN = "INSERT INTO %s VALUES (?, ?, ?, ?)";
    private static final String DELETE_TOKEN = "DELETE FROM %s WHERE Owner = ? AND Token = ?";

    private static final String INSERT_HISTORY_DENY = "INSERT INTO %s(User, Time) VALUES (?, ?)";
    private static final String INSERT_HISTORY_ALLOW = "INSERT INTO %s VALUES (?, ?, ?)";
    private static final String SELECT_HISTORY_BY_USER = "SELECT * FROM %s WHERE User = ? ORDER BY Time DESC";
    private static final String SELECT_LATEST_HISTORY_ENTRY_BY_USER = "SELECT * FROM %s WHERE User = ? ORDER BY Time DESC LIMIT 1";

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

    public SQLiteDatabase(final ILogger logger, final Executor executor, final TickTimer cacheTimer, final PluginSettings pluginSettings,
        final DatabaseSettings settings, final File dataDirectory) {
        super(executor);
        this.cacheTimer = cacheTimer;
        this.logger = logger;
        cacheTimer.add(this);
        this.wildcardCache = new ThreadSafeCache<>(UUID.class,
            Math.max(120, Math.abs(pluginSettings.getInteger("cache.wildcard.time", 1800))));
        this.tokenCache = new ThreadSafeCache<>(UUID.class, Math.max(600, Math.abs(pluginSettings.getInteger("cache.token.time", 3600))));
        this.historyCache = new ThreadSafeCache<>(UUID.class, Math.max(60, Math.abs(pluginSettings.getInteger("cache.history.time", 300))));
        this.tokenTable = settings.getString("sqlite.table.token", "WildcardTokens");
        this.historyTable = settings.getString("sqlite.table.history", "WildcardHistory");
        final HikariConfig config = new HikariConfig();
        config.setConnectionTimeout(Math.max(1000, Math.abs(settings.getInteger("sqlite.timeout.connection", 7500))));
        config.setMaximumPoolSize(Math.max(1, Math.min(16, settings.getInteger("sqlite.pool.max", 4))));
        config.setMinimumIdle(Math.max(1, Math.min(16, settings.getInteger("sqlite.pool.min", 1))));
        config.setPoolName("Wildcard");
        final File file = new File(dataDirectory, "wildcard.db");
        Files.createFile(file);
        config.setJdbcUrl("jdbc:sqlite://" + file.getAbsolutePath());
        this.pool = new HikariPool(config);
        try (Connection connection = pool.getConnection(15000)) {
            final PreparedStatement statement = connection.prepareStatement("/* ping */ SELECT 1");
            statement.setQueryTimeout(15);
            statement.executeQuery();
        } catch (final SQLException exp) {
            close();
            throw new DatabaseInitializationException("SQLite connection test failed", exp);
        }
        try {
            setup();
        } catch (final SQLException exp) {
            close();
            throw new DatabaseInitializationException("SQLite database setup failed", exp);
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

    private void setup() throws SQLException {
        try (Connection connection = pool.getConnection()) {
            connection.prepareStatement(String.format(CREATE_TABLE, tokenTable,
                "(Owner BINARY(16) NOT NULL, Token BINARY(20) NOT NULL, Uses INT NOT NULL, Expires VARCHAR(22), CONSTRAINT UToken UNIQUE (Token), CONSTRAINT POwner PRIMARY KEY (Owner))"))
                .executeUpdate();
            connection.prepareStatement(
                String.format(CREATE_TABLE, historyTable, "(User BINARY(16) NOT NULL, TokenOwner BINARY(16), Time VARCHAR(22) NOT NULL)"))
                .executeUpdate();
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
                statement.setBytes(1, UUIDHelper.fromUniqueId(uniqueId));
                final ResultSet set = statement.executeQuery();
                if (set.next()) {
                    final boolean state = set.getBytes("TokenOwner") != null;
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
        return getToken(uniqueId).thenComposeAsync(token -> CompletableFuture.completedStage(token != null), executor);
    }

    @Override
    public CompletableFuture<Token> getToken(final UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            if (tokenCache.has(uniqueId)) {
                return tokenCache.get(uniqueId);
            }
            try (Connection connection = pool.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(selectTokenByUser);
                statement.setBytes(1, UUIDHelper.fromUniqueId(uniqueId));
                final ResultSet set = statement.executeQuery();
                if (set.next()) {
                    final OffsetDateTime time = TimeHelper.fromString(set.getString("Expires"));
                    final String tokenHash = Hex.encodeHexString(set.getBytes("Token"));
                    final UUID owner = UUIDHelper.toUniqueId(set.getBytes("Owner"));
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
                logger.log(LogTypeId.DEBUG, exp);
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
                statement.setBytes(1, UUIDHelper.fromUniqueId(uniqueId));
                statement.setBytes(2, Hex.decodeHex(tokenHash));
                statement.executeUpdate();
            } catch (SQLException | DecoderException exp) {
                logger.log(LogTypeId.WARNING,
                    "Failed to delete token information of '" + tokenHash + "' owned by '" + uniqueId.toString() + "' from MySQL");
                logger.log(LogTypeId.DEBUG, exp);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Token> getTokenOrGenerate(final UUID uniqueId, final int uses, final OffsetDateTime expires) {
        return getToken(uniqueId)
            .thenComposeAsync(token -> token != null ? CompletableFuture.completedStage(token) : CompletableFuture.supplyAsync(() -> {
                byte[] tokenRaw = DigestUtils.sha1(tokenGen.makeKey(12));
                try (Connection connection = pool.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(selectTokenByToken);
                    statement.setBytes(1, tokenRaw);
                    while (statement.executeQuery().next()) {
                        tokenRaw = DigestUtils.sha1(tokenGen.makeKey(12));
                        statement.setBytes(1, tokenRaw);
                    }
                    statement = connection.prepareStatement(insertToken);
                    statement.setBytes(1, UUIDHelper.fromUniqueId(uniqueId));
                    statement.setBytes(2, tokenRaw);
                    statement.setInt(3, uses);
                    statement.setObject(4, TimeHelper.toString(expires));
                    statement.executeUpdate();
                    final Token generatedToken = new Token(uniqueId, Hex.encodeHexString(tokenRaw), uses, expires);
                    tokenCache.put(uniqueId, token);
                    return generatedToken;
                } catch (final SQLException exp) {
                    logger.log(LogTypeId.WARNING, "Failed to store token created for '" + uniqueId.toString() + "' to MySQL");
                    logger.log(LogTypeId.DEBUG, exp);
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
                statement.setBytes(2, Hex.decodeHex(token.getToken()));
                statement.executeUpdate();
            } catch (SQLException | DecoderException exp) {
                logger.log(LogTypeId.WARNING,
                    "Failed to send update of Token '" + token.getToken() + "' of '" + token.getOwner().toString() + "' to MySQL");
            }
        }, executor);
    }

    @Override
    public CompletableFuture<RequestResult> deny(final UUID uniqueId) {
        return isAllowed(uniqueId).thenComposeAsync(state -> {
            if (!state) {
                return CompletableFuture.completedStage(RequestResult.KNOWN);
            }
            try (Connection connection = pool.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(insertHistoryDeny);
                statement.setBytes(1, UUIDHelper.fromUniqueId(uniqueId));
                statement.setObject(2, TimeHelper.toString(OffsetDateTime.now()));
                statement.executeUpdate();
                wildcardCache.put(uniqueId, false);
                return CompletableFuture.completedStage(RequestResult.SUCCESS);
            } catch (final SQLException exp) {
                logger.log(LogTypeId.WARNING, "Failed to insert history (deny) of '" + uniqueId.toString() + "' to MySQL");
            }
            return CompletableFuture.completedStage(RequestResult.FAILED);
        }, executor);
    }

    @Override
    public CompletableFuture<RequestResult> allow(final UUID uniqueId, final UUID targetId) {
        return isAllowed(uniqueId).thenComposeAsync(state -> {
            if (state) {
                return CompletableFuture.completedStage(RequestResult.KNOWN);
            }
            try (Connection connection = pool.getConnection()) {
                final PreparedStatement statement = connection.prepareStatement(insertHistoryAllow);
                statement.setBytes(1, UUIDHelper.fromUniqueId(uniqueId));
                statement.setBytes(2, UUIDHelper.fromUniqueId(targetId));
                statement.setObject(3, TimeHelper.toString(OffsetDateTime.now()));
                statement.executeUpdate();
                wildcardCache.put(uniqueId, true);
                return CompletableFuture.completedStage(RequestResult.SUCCESS);
            } catch (final SQLException exp) {
                logger.log(LogTypeId.WARNING, "Failed to insert history (allow) of '" + uniqueId.toString() + "' to SQLite");
            }
            return CompletableFuture.completedStage(RequestResult.FAILED);
        }, executor);
    }

    @Override
    public CompletableFuture<RequestResult> allow(final UUID uniqueId, final String tokenHash) {
        return isAllowed(uniqueId).thenComposeAsync(state -> {
            if (state) {
                return CompletableFuture.completedStage(RequestResult.KNOWN);
            }
            try (Connection connection = pool.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(selectTokenByToken);
                statement.setBytes(1, Hex.decodeHex(tokenHash));
                final ResultSet set = statement.executeQuery();
                if (!set.next()) {
                    return CompletableFuture.completedStage(RequestResult.FAILED);
                }
                final UUID targetId = UUIDHelper.toUniqueId(set.getBytes("Owner"));
                final Token token = tokenCache.has(uniqueId) ? tokenCache.get(uniqueId)
                    : new Token(targetId, tokenHash, set.getInt("Uses"), TimeHelper.fromString(set.getString("Expires")));
                if (token.use() == -1) {
                    deleteToken(token);
                    return CompletableFuture.completedFuture(RequestResult.FAILED);
                }
                updateToken(token);
                statement = connection.prepareStatement(insertHistoryAllow);
                statement.setBytes(1, UUIDHelper.fromUniqueId(uniqueId));
                statement.setBytes(2, UUIDHelper.fromUniqueId(targetId));
                statement.setObject(3, TimeHelper.toString(OffsetDateTime.now()));
                statement.executeUpdate();
                wildcardCache.put(uniqueId, true);
                return CompletableFuture.completedStage(RequestResult.SUCCESS);
            } catch (SQLException | DecoderException exp) {
                logger.log(LogTypeId.WARNING, "Failed to insert history (allow) of '" + uniqueId.toString() + "' to MySQL");
            }
            return CompletableFuture.completedStage(RequestResult.FAILED);
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
                statement.setBytes(1, UUIDHelper.fromUniqueId(uniqueId));
                final ResultSet set = statement.executeQuery();
                while (set.next()) {
                    final OffsetDateTime time = TimeHelper.fromString(set.getString("Time"));
                    final byte[] target = set.getBytes("TokenOwner");
                    list.add(new HistoryEntry(uniqueId, target == null ? null : UUIDHelper.toUniqueId(target), time));
                }
            } catch (final SQLException exp) {
                logger.log(LogTypeId.WARNING, "Failed to retrieve history of '" + uniqueId.toString() + "' from MySQL");
            }
            final HistoryEntry[] entries = list.toArray(HistoryEntry[]::new);
            if (entries.length != 0) {
                historyCache.put(uniqueId, entries);
            }
            return list.toArray(HistoryEntry[]::new);
        }, executor);
    }

}
