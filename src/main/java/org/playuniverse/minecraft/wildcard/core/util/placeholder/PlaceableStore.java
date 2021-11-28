package org.playuniverse.minecraft.wildcard.core.util.placeholder;

public interface PlaceableStore {

    default Placeable getPlaceable(final String key) {
        return getPlaceable(key, false);
    }

    Placeable getPlaceable(String key, boolean flag);

    Placeable[] placeableArray();

}
