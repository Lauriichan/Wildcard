package me.lauriichan.minecraft.wildcard.core.settings;

import me.lauriichan.minecraft.wildcard.core.data.setting.Category;

public class RatelimitSettings extends CategorizedSettings {

    public static final Category CATEGORY = new Category("ratelimit");

    public RatelimitSettings() {
        super(CATEGORY);
    }

}
