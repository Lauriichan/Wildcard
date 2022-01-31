package me.lauriichan.minecraft.wildcard.core.data.container.nbt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

import com.syntaxphoenix.syntaxapi.nbt.NbtBigDecimal;
import com.syntaxphoenix.syntaxapi.nbt.NbtBigInt;
import com.syntaxphoenix.syntaxapi.nbt.NbtByte;
import com.syntaxphoenix.syntaxapi.nbt.NbtByteArray;
import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtDouble;
import com.syntaxphoenix.syntaxapi.nbt.NbtFloat;
import com.syntaxphoenix.syntaxapi.nbt.NbtInt;
import com.syntaxphoenix.syntaxapi.nbt.NbtIntArray;
import com.syntaxphoenix.syntaxapi.nbt.NbtList;
import com.syntaxphoenix.syntaxapi.nbt.NbtLong;
import com.syntaxphoenix.syntaxapi.nbt.NbtLongArray;
import com.syntaxphoenix.syntaxapi.nbt.NbtShort;
import com.syntaxphoenix.syntaxapi.nbt.NbtString;
import com.syntaxphoenix.syntaxapi.nbt.NbtTag;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;
import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

import me.lauriichan.minecraft.wildcard.core.data.container.AbstractDataAdapter;
import me.lauriichan.minecraft.wildcard.core.data.container.AbstractDataContainer;
import me.lauriichan.minecraft.wildcard.core.data.container.api.IDataContainer;

public class NbtAdapter<P, C extends NbtTag> extends AbstractDataAdapter<P, C, NbtTag> {

    protected NbtAdapter(final Class<P> primitiveType, final Class<C> resultType, final Function<P, C> builder,
        final Function<C, P> extractor) {
        super(primitiveType, resultType, builder, extractor);
    }

    @Override
    public Class<NbtTag> getBaseType() {
        return NbtTag.class;
    }

    /*
     * 
     */

    @SuppressWarnings({
        "rawtypes",
        "unchecked"
    })
    protected static AbstractDataAdapter<?, ? extends NbtTag, NbtTag> createAdapter(final NbtAdapterRegistry registry, Class<?> type) {
        type = Primitives.fromPrimitive(type);

        /*
         * Numbers
         */

        if (Objects.equals(Boolean.class, type)) {
            return new NbtAdapter<>(Boolean.class, NbtByte.class, state -> new NbtByte((byte) (state ? 1 : 0)),
                value -> value.getByteValue() == 1);
        }

        if (Objects.equals(Byte.class, type)) {
            return new NbtAdapter<>(Byte.class, NbtByte.class, NbtByte::new, NbtByte::getByteValue);
        }

        if (Objects.equals(Short.class, type)) {
            return new NbtAdapter<>(Short.class, NbtShort.class, NbtShort::new, NbtShort::getShortValue);
        }

        if (Objects.equals(Integer.class, type)) {
            return new NbtAdapter<>(Integer.class, NbtInt.class, NbtInt::new, NbtInt::getIntValue);
        }

        if (Objects.equals(Long.class, type)) {
            return new NbtAdapter<>(Long.class, NbtLong.class, NbtLong::new, NbtLong::getLongValue);
        }

        if (Objects.equals(BigInteger.class, type)) {
            return new NbtAdapter<>(BigInteger.class, NbtBigInt.class, NbtBigInt::new, NbtBigInt::getInteger);
        }

        if (Objects.equals(Float.class, type)) {
            return new NbtAdapter<>(Float.class, NbtFloat.class, NbtFloat::new, NbtFloat::getFloatValue);
        }

        if (Objects.equals(Double.class, type)) {
            return new NbtAdapter<>(Double.class, NbtDouble.class, NbtDouble::new, NbtDouble::getDoubleValue);
        }

        if (Objects.equals(BigDecimal.class, type)) {
            return new NbtAdapter<>(BigDecimal.class, NbtBigDecimal.class, NbtBigDecimal::new,
                NbtBigDecimal::getDecimal);
        }

        /*
         * String
         */

        if (Objects.equals(String.class, type)) {
            return new NbtAdapter<>(String.class, NbtString.class, NbtString::new, NbtString::getValue);
        }

        /*
         * Number Arrays
         */

        if (Objects.equals(byte[].class, type)) {
            return new NbtAdapter<>(byte[].class, NbtByteArray.class, NbtByteArray::new, NbtByteArray::getValue);
        }

        if (Objects.equals(int[].class, type)) {
            return new NbtAdapter<>(int[].class, NbtIntArray.class, NbtIntArray::new, NbtIntArray::getValue);
        }

        if (Objects.equals(long[].class, type)) {
            return new NbtAdapter<>(long[].class, NbtLongArray.class, NbtLongArray::new, NbtLongArray::getValue);
        }

        /*
         * Complex Arrays
         */

        if (Objects.equals(IDataContainer[].class, type)) {
            return new NbtAdapter<>(IDataContainer[].class, NbtList.class, containers -> {
                final NbtList<NbtCompound> list = new NbtList<>(NbtType.COMPOUND);
                for (final IDataContainer container : containers) {
                    list.add(toNbtCompound(registry, container));
                }
                return list;
            }, list -> {
                if (list.getElementType() != NbtType.COMPOUND) {
                    return new IDataContainer[0];
                }
                final NbtList<NbtCompound> nbtList = list;
                final ArrayList<IDataContainer> containers = new ArrayList<>();
                for (final NbtTag tag : nbtList) {
                    containers.add(fromNbtCompound(registry, (NbtCompound) tag));
                }
                return containers.toArray(new IDataContainer[0]);
            });
        }

        if (Objects.equals(AbstractDataContainer[].class, type)) {
            return new NbtAdapter<>(AbstractDataContainer[].class, NbtList.class, containers -> {
                final NbtList<NbtCompound> list = new NbtList<>(NbtType.COMPOUND);
                for (final AbstractDataContainer container : containers) {
                    list.add(toNbtCompound(registry, container));
                }
                return list;
            }, list -> {
                if (list.getElementType() != NbtType.COMPOUND) {
                    return new AbstractDataContainer[0];
                }
                final NbtList<NbtCompound> nbtList = list;
                final ArrayList<NbtContainer> containers = new ArrayList<>();
                for (final NbtTag tag : nbtList) {
                    containers.add(fromNbtCompound(registry, (NbtCompound) tag));
                }
                return containers.toArray(new AbstractDataContainer[0]);
            });
        }

        if (Objects.equals(NbtContainer[].class, type)) {
            return new NbtAdapter<>(NbtContainer[].class, NbtList.class, containers -> {
                final NbtList<NbtCompound> list = new NbtList<>(NbtType.COMPOUND);
                for (final NbtContainer container : containers) {
                    list.add(toNbtCompound(registry, container));
                }
                return list;
            }, list -> {
                if (list.getElementType() != NbtType.COMPOUND) {
                    return new NbtContainer[0];
                }
                final NbtList<NbtCompound> nbtList = list;
                final ArrayList<NbtContainer> containers = new ArrayList<>();
                for (final NbtTag tag : nbtList) {
                    containers.add(fromNbtCompound(registry, (NbtCompound) tag));
                }
                return containers.toArray(new NbtContainer[0]);
            });
        }

        /*
         * Complex
         */

        if (IDataContainer.class.isAssignableFrom(type)) {
            return new NbtAdapter<>(IDataContainer.class, NbtCompound.class,
                container -> toNbtCompound(registry, container), compound -> fromNbtCompound(registry, compound));
        }

        /*
         * NbtTag
         */

        if (NbtTag.class.isAssignableFrom(type)) {
            return new NbtAdapter<>(NbtTag.class, NbtTag.class, tag -> tag, tag -> tag);
        }

        return null;
    }

    private static NbtCompound toNbtCompound(final NbtAdapterRegistry registry, final IDataContainer container) {
        if (container instanceof NbtContainer) {
            return ((NbtContainer) container).getRoot().clone();
        }
        final NbtCompound compound = new NbtCompound();
        for (final String key : container.getKeyspaces()) {
            final Object object = container.get(key);
            final NbtTag tag = registry.wrap(object);
            if (tag != null) {
                compound.set(key, tag);
            }
        }
        return compound;
    }

    private static NbtContainer fromNbtCompound(final NbtAdapterRegistry registry, final NbtCompound compound) {
        return new NbtContainer(compound.clone(), registry);
    }

}
