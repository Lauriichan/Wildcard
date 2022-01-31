package me.lauriichan.minecraft.wildcard.core.data.container.api;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface IDataFactory<B> {

    IDataAdapterRegistry<B> getRegistry();

    IDataFactory<B> toFile(IDataContainer container, File file);

    IDataFactory<B> toStream(IDataContainer container, OutputStream stream);

    IDataFactory<B> toString(IDataContainer container, StringBuilder builder);

    IDataFactory<B> fromFile(IDataContainer container, File file);

    IDataFactory<B> fromStream(IDataContainer container, InputStream stream);

    IDataFactory<B> fromString(IDataContainer container, String string);

}
