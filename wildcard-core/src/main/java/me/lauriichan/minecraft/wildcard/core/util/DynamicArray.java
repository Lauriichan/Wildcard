package me.lauriichan.minecraft.wildcard.core.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntFunction;

@SuppressWarnings("unchecked")
public final class DynamicArray<E> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock write = lock.writeLock();
    private final Lock read = lock.readLock();

    private final int expand;

    private Object[] array;
    private int size = 0;

    public DynamicArray() {
        this(4);
    }

    public DynamicArray(final int size) {
        this(size, size / 2);
    }

    public DynamicArray(final int size, final int expand) {
        this.array = new Object[size];
        this.expand = expand;
    }

    public E get(final int index) {
        if (isNull(index)) {
            return null;
        }
        read.lock();
        try {
            return (E) array[index];
        } finally {
            read.unlock();
        }
    }

    public boolean isSet(final int index) {
        read.lock();
        try {
            return index < size && index >= 0;
        } finally {
            read.unlock();
        }
    }

    public boolean isNull(final int index) {
        read.lock();
        try {
            if (index > size || index < 0) {
                return true;
            }
            return array[index] == null;
        } finally {
            read.unlock();
        }
    }

    public boolean set(final int index, final E value) {
        if (index > size || index < 0) {
            return false;
        }
        array[index] = value;
        return true;
    }

    public DynamicArray<E> add(final E value) {
        expand();
        read.lock();
        int index;
        try {
            index = size;
        } finally {
            read.unlock();
        }
        write.lock();
        try {
            array[index] = value;
            size += 1;
        } finally {
            write.unlock();
        }
        return this;
    }

    public E remove(final int index) {
        if (!isSet(index)) {
            return null;
        }
        E value;
        read.lock();
        try {
            value = (E) array[index];
        } finally {
            read.unlock();
        }
        write.lock();
        try {
            for (int x = index + 1; x < size; x++) {
                array[x - 1] = array[x];
            }
            array[size] = null;
            size--;
        } finally {
            write.unlock();
        }
        return value;
    }

    public boolean remove(final E value) {
        final int index = indexOf(value);
        if (index == -1) {
            return false;
        }
        write.lock();
        try {
            for (int x = index + 1; x < size; x++) {
                array[x - 1] = array[x];
            }
            array[size] = null;
            size--;
        } finally {
            write.unlock();
        }
        return true;
    }

    public int indexOf(final E value) {
        read.lock();
        try {
            for (int index = 0; index < size; index++) {
                if (array[index] == value) {
                    return index;
                }
            }
        } finally {
            read.unlock();
        }
        return -1;
    }

    private void expand() {
        read.lock();
        if (size == array.length) {
            read.unlock();
            write.lock();
            try {
                final Object[] expanded = new Object[size + expand];
                System.arraycopy(array, 0, expanded, 0, size);
                array = expanded;
            } finally {
                write.unlock();
            }
            return;
        }
        read.unlock();
    }

    public Object[] asArray() {
        read.lock();
        try {
            final Object[] output = new Object[size];
            System.arraycopy(array, 0, output, 0, size);
            return output;
        } finally {
            read.unlock();
        }
    }

    public E[] asArray(final IntFunction<E[]> function) {
        read.lock();
        try {
            final E[] output = function.apply(size);
            System.arraycopy(array, 0, output, 0, size);
            return output;
        } finally {
            read.unlock();
        }
    }

}
