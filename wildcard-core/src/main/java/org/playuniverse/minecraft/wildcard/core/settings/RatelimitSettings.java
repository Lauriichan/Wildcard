package org.playuniverse.minecraft.wildcard.core.settings;

import org.playuniverse.minecraft.wildcard.core.data.setting.Category;

public class RatelimitSettings extends CategorizedSettings {

    public static final Category CATEGORY = new Category("ratelimit");

    public RatelimitSettings() {
        super(CATEGORY);
    }

}
