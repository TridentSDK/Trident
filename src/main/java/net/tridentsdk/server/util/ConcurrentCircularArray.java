/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.util;

import com.google.code.tempusfugit.concurrency.annotations.ThreadSafe;


import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread safe implementation of a Circular array
 *
 * <p>A simple data structure that will overwrite the oldest value when a new one is added, keeping it at a
 * fixed length</p>
 */
@ThreadSafe
public class ConcurrentCircularArray<E> {

    protected final AtomicReferenceArray<E> backing;
    private int current;
    private int size;

    private final int maxSize;

    private final ReadWriteLock rwLock;

    public ConcurrentCircularArray(final int length) {
        backing = new AtomicReferenceArray<E>(length);
        // The position of the value to write
        current = 0;
        size = 0;

        maxSize = length;

        rwLock = new ReentrantReadWriteLock();
    }

    public boolean add(E element) {

        Lock lock = rwLock.writeLock();
        lock.lock();
        try {
            if (size < maxSize) {
                backing.set(current, element);
                size++;
                if (current + 1 == maxSize) {
                    current = 0;
                } else {
                    current++;
                }
            } else {
                backing.set(current, element);
                if (current + 1 == maxSize) {
                    current = 0;
                } else {
                    current++;
                }
            }

        } finally {
            lock.unlock();
        }
        return true;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Iterator<E> iterator() {

        Lock lock = rwLock.readLock();
        lock.lock();
        Iterator<E> retVal = null;

        try {
            retVal = new CircularArrayIterator<E>(this);
        } finally {
            lock.unlock();
        }

        return retVal;
    }

    public void clear() {
        Lock lock = rwLock.writeLock();
        lock.lock();
        try {
            for (int i = 0; i < maxSize; i++) {
                backing.set(i, null);
            }
            size = 0;
            current = 0;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes the value from the circular array
     *
     * <p>Does not collapse the array, only replaces the value with null</p>
     * @param i the index of the value to replace
     * @param value if this removal is to be checked for modification, the value that is expected, otherwise, null
     * @return if a specific value was expected, whether it was successfully removed
     */
    protected boolean remove (int i, E value) {
        Lock lock = rwLock.writeLock();
        lock.lock();
        try {
            if (value == null) {
                backing.set(i,null);
                size--;
            }
            else {
                boolean retVal = backing.compareAndSet(i, value, null);
                if(retVal) {
                    size--;
                }
                return retVal;
            }
        } finally {
            lock.unlock();
        }

        return true;
    }

    /**
     * Whether or not this Object has all of its elements filled up, and an add will cause an overwrite to occur
     *
     * @return whether or not this is full
     */
    public boolean isFull() {
        return size == maxSize;
    }

    public int getMaxSize () {
        return maxSize;
    }
}
