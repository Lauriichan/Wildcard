package org.playuniverse.minecraft.wildcard.core.util;

import java.util.concurrent.Future;

import com.syntaxphoenix.syntaxapi.utils.general.Status;

@FunctionalInterface
@SuppressWarnings("rawtypes")
public interface IWaitFunction<E> {

    IWaitFunction<Status> STATUS = Status::isDone;
    IWaitFunction<Future> FUTURE = Future::isDone;

    /*
     * 
     */

    long WAIT_INTERVAL = 10L;
    int WAIT_INFINITE = -1;

    default void await(final E waited) {
        await(waited, WAIT_INTERVAL);
    }

    default void await(final E waited, final long interval) {
        await(waited, WAIT_INTERVAL, WAIT_INFINITE);
    }

    default void await(final E waited, final long interval, int length) {
        while (!isDone(waited)) {
            try {
                Thread.sleep(interval);
            } catch (final InterruptedException ignore) {
                break;
            }
            if (length == -1) {
                continue;
            }
            if (length-- == 0) {
                break;
            }
        }
    }

    boolean isDone(E waited);

}