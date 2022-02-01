package me.lauriichan.minecraft.wildcard.core.util.reflection.handle;

public class ClassLookupCache extends AbstractClassLookupCache<ClassLookup> {

    @Override
    protected ClassLookup create(final Class<?> clazz) {
        return ClassLookup.of(clazz);
    }

    @Override
    protected ClassLookup create(final String path) {
        return ClassLookup.of(path);
    }

}
