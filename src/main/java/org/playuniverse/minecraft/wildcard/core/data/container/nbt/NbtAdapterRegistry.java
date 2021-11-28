package org.playuniverse.minecraft.wildcard.core.data.container.nbt;

import java.util.function.Function;

import org.playuniverse.minecraft.wildcard.core.data.container.AbstractDataAdapterRegistry;
import org.playuniverse.minecraft.wildcard.core.data.container.AbstractDataContainer;
import org.playuniverse.minecraft.wildcard.core.data.container.api.IDataAdapter;
import org.playuniverse.minecraft.wildcard.core.data.container.api.IDataContainer;

import com.syntaxphoenix.syntaxapi.nbt.NbtTag;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;

public class NbtAdapterRegistry extends AbstractDataAdapterRegistry<NbtTag> {

    public NbtAdapterRegistry() {
        adapters.add(build(IDataContainer.class));
        adapters.add(build(IDataContainer[].class));
        adapters.add(build(AbstractDataContainer[].class));
        adapters.add(build(NbtContainer[].class));
    }

    @Override
    public Object extract(final NbtTag base) {
        if (base.getType() == NbtType.END) {
            return null;
        }
        return super.extract(base);
    }

    @Override
    public Class<NbtTag> getBase() {
        return NbtTag.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <P, C extends NbtTag> IDataAdapter<P, C, NbtTag> build(final Class<?> clazz) {
        return (IDataAdapter<P, C, NbtTag>) NbtAdapter.createAdapter(this, clazz);
    }

    @Override
    public <P, C extends NbtTag> IDataAdapter<P, C, NbtTag> create(final Class<P> primitiveType, final Class<C> complexType,
        final Function<P, C> builder, final Function<C, P> extractor) {
        return new NbtAdapter<>(primitiveType, complexType, builder, extractor);
    }

}
