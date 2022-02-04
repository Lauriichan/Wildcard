package me.lauriichan.minecraft.wildcard.core.data.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executor;

public abstract class SQLDatabase extends Database {

    public SQLDatabase(Executor executor) {
        super(executor);
    }

    public abstract Connection getConnection() throws SQLException;
    
    public abstract String getTableName(SQLTable table);

}
