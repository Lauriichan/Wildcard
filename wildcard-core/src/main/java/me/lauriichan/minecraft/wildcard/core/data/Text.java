package me.lauriichan.minecraft.wildcard.core.data;

import me.lauriichan.minecraft.wildcard.core.data.migration.type.SQLMigrationType;
import me.lauriichan.minecraft.wildcard.core.data.storage.Database;
import me.lauriichan.minecraft.wildcard.migration.Date;
import me.lauriichan.minecraft.wildcard.migration.Migration;
import me.lauriichan.minecraft.wildcard.migration.MigrationProvider;

@Migration(source = Database.class, type = SQLMigrationType.class)
public final class Text extends MigrationProvider {

    @Override
    protected long getDate() {
        return Date.of(23, 13, 3, 2, 2022);
    }

}
