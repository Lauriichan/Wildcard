package org.playuniverse.minecraft.wildcard.core.data.container.nbt;

import java.util.Set;

import org.playuniverse.minecraft.wildcard.core.data.container.AbstractDataContainer;
import org.playuniverse.minecraft.wildcard.core.data.container.api.IDataAdapterContext;
import org.playuniverse.minecraft.wildcard.core.data.container.api.IDataAdapterRegistry;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtTag;
import com.syntaxphoenix.syntaxapi.nbt.utils.NbtStorage;

public class NbtContainer extends AbstractDataContainer<NbtTag> implements IDataAdapterContext, NbtStorage<NbtCompound> {

    protected final NbtCompound root;

    public NbtContainer(final IDataAdapterRegistry<NbtTag> registry) {
        this(new NbtCompound(), registry);
    }

    protected NbtContainer(final NbtCompound root, final IDataAdapterRegistry<NbtTag> registry) {
        super(registry);
        this.root = root;
    }

    @Override
    public NbtContainer newContainer() {
        return new NbtContainer(registry);
    }

    @Override
    public IDataAdapterContext getContext() {
        return this;
    }

    public NbtCompound getRoot() {
        return root;
    }

    @Override
    public void fromNbt(final NbtCompound nbt) {
        root.clear();
        for (final String key : nbt.getKeys()) {
            set(key, nbt.get(key));
        }
    }

    @Override
    public NbtCompound asNbt() {
        return root.clone();
    }

    @Override
    public boolean has(final String key) {
        return root.hasKey(key);
    }

    @Override
    public NbtTag getRaw(final String key) {
        return root.get(key);
    }

    @Override
    public boolean remove(final String key) {
        return root.remove(key) != null;
    }

    @Override
    public void set(final String key, final NbtTag value) {
        if (value == null) {
            remove(key);
            return;
        }
        root.set(key, value);
    }

    @Override
    public Set<String> getKeyspaces() {
        return root.getKeys();
    }

    @Override
    public boolean isEmpty() {
        return root.isEmpty();
    }

    @Override
    public int size() {
        return root.size();
    }

}
