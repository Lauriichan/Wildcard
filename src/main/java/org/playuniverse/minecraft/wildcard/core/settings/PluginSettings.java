package org.playuniverse.minecraft.wildcard.core.settings;

import org.playuniverse.minecraft.wildcard.core.data.setting.Category;

public final class PluginSettings extends CategorizedSettings {

    public static final Category CATEGORY = new Category("plugin");

    public PluginSettings() {
        super(CATEGORY);
    }

}