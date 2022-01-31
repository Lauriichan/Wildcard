package me.lauriichan.minecraft.wildcard.core.util;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.syntaxphoenix.syntaxapi.utils.general.Status;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public final class Awaiter<T> {

    private static final ConcurrentHashMap<Class<?>, IWaitFunction<?>> FUNCTIONS = new ConcurrentHashMap<>();

    static {
        register(Status.class, IWaitFunction.STATUS);
        register(Future.class, IWaitFunction.FUTURE);
    }

    public static Awaiter<?> of(final Object waited) {
        final Class<?> clazz = waited.getClass();
        final Iterator<Class<?>> keys = FUNCTIONS.keys().asIterator();
        while (keys.hasNext()) {
            final Class<?> target = keys.next();
            if (!target.isAssignableFrom(clazz)) {
                continue;
            }
            return build(target, waited, FUNCTIONS.get(target));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <E> Awaiter<E> build(final Class<E> clazz, final Object waited, final IWaitFunction<?> function) {
        if (function == null || clazz == null || waited == null) {
            return null;
        }
        return new Awaiter<>(clazz.cast(waited), (IWaitFunction<E>) function);
    }

    public static <E> void register(final Class<E> clazz, final IWaitFunction<E> function) {
        if (FUNCTIONS.containsKey(clazz)) {
            return;
        }
        FUNCTIONS.put(clazz, function);
    }

    private final Container<T> waited = Container.of();
    private final IWaitFunction<T> function;

    private Awaiter(final T waited, final IWaitFunction<T> function) {
        this.waited.replace(waited);
        this.function = function;
    }

    public boolean now(final T object) {
        if (waited.isPresent()) {
            return false;
        }
        waited.replace(object);
        return true;
    }

    public boolean isAvailable() {
        return waited.isPresent();
    }

    public boolean isDone() {
        if (!isAvailable()) {
            return true;
        }
        return function.isDone(waited.get());
    }

    public boolean await() {
        if (!isAvailable()) {
            return true;
        }
        function.await(waited.get());
        return done();
    }

    public boolean await(final long interval) {
        if (!isAvailable()) {
            return true;
        }
        function.await(waited.get(), interval);
        return done();
    }

    public boolean await(final long interval, final int length) {
        if (!isAvailable()) {
            return true;
        }
        function.await(waited.get(), interval, length);
        return done();
    }

    private boolean done() {
        try {
            return isDone();
        } finally {
            waited.replace(null);
        }
    }

}