package me.lauriichan.minecraft.wildcard.core.util.placeholder;

public interface PlaceholderStore extends PlaceableStore {

    void setPlaceholder(Placeholder value);

    Placeholder getPlaceholder(String key);

    Placeholder[] placeholderArray();

    @Override
    default Placeable getPlaceable(final String key, final boolean flag) {
        return getPlaceholder(key);
    }

    @Override
    default Placeable[] placeableArray() {
        return placeholderArray();
    }

}
