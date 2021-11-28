package org.playuniverse.minecraft.wildcard.core.settings;

import org.playuniverse.minecraft.wildcard.core.data.setting.Category;

public final class DatabaseSettings extends CategorizedSettings {

    public static final Category CATEGORY = new Category("database");

    public DatabaseSettings() {
        super(CATEGORY);
    }

}