package org.playuniverse.minecraft.wildcard.core.util.inject;

import org.playuniverse.minecraft.wildcard.core.util.reflection.ClassLookupProvider;
import org.playuniverse.minecraft.wildcard.core.util.registry.TypedRegistry;

public class Injections {

    private final TypedRegistry<Injector<?>> injectors = new TypedRegistry<>();
    private final ClassLookupProvider provider;

    private boolean setup = false;

    public Injections(final ClassLookupProvider provider) {
        this.provider = provider;
    }

    public ClassLookupProvider getProvider() {
        return provider;
    }

    public boolean register(final Injector<?> injector) {
        if (!injectors.register(injector)) {
            return false;
        }
        if (!injector.isSetup() && setup) {
            injector.setup(provider);
        }
        return true;
    }

    public void setup() {
        if (setup) {
            return;
        }
        setup = true;
        for (final Injector<?> injector : injectors.values()) {
            if (injector.isSetup()) {
                continue;
            }
            injector.setup(provider);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> boolean inject(final T object) {
        final Injector<?> injector = injectors.getFor(object);
        if (injector == null) {
            return false;
        }
        ((Injector<T>) injector).inject(provider, object);
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> boolean uninject(final T object) {
        final Injector<?> injector = injectors.getFor(object);
        if (injector == null) {
            return false;
        }
        ((Injector<T>) injector).uninject(provider, object);
        return true;
    }

    public void uninjectAll() {
        for (final Injector<?> injector : injectors.values()) {
            injector.uninjectAll(provider);
        }
    }

    public void dispose() {
        for (final Injector<?> injector : injectors.values()) {
            injector.dispose();
        }
        injectors.dispose();
    }

}