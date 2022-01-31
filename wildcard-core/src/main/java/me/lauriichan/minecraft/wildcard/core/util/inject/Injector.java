package me.lauriichan.minecraft.wildcard.core.util.inject;

import me.lauriichan.minecraft.wildcard.core.util.reflection.ClassLookupProvider;
import me.lauriichan.minecraft.wildcard.core.util.registry.ITyped;

public abstract class Injector<T> implements ITyped<T> {

    private boolean setup = false;

    public final boolean isSetup() {
        return setup;
    }

    public final void setup(final ClassLookupProvider provider) {
        if (setup) {
            throw new IllegalStateException("Is already setup!");
        }
        setup = true;
        onSetup(provider);
    }

    protected void onSetup(final ClassLookupProvider provider) {}

    public boolean isCompatible(final ClassLookupProvider provider) {
        return true;
    }

    public final boolean inject(final ClassLookupProvider provider, final T object) {
        if (setup) {
            inject0(provider, object);
            return true;
        }
        return false;
    }

    public final boolean uninject(final ClassLookupProvider provider, final T object) {
        if (setup) {
            uninject0(provider, object);
            return true;
        }
        return false;

    }

    public final boolean uninjectAll(final ClassLookupProvider provider) {
        if (setup) {
            uninjectAll0(provider);
            return true;
        }
        return false;
    }

    protected void inject0(final ClassLookupProvider provider, final T object) {}

    protected void uninject0(final ClassLookupProvider provider, final T object) {}

    protected void uninjectAll0(final ClassLookupProvider provider) {}

    protected void dispose() {}

}