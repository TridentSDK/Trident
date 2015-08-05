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

import com.google.common.collect.Iterators;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread safe implementation of a Circular array
 *
 * <p>A simple data structure that will overwrite the oldest value when a new one is added, keeping it at a
 * fixed length</p>
 */
@ThreadSafe
public class ConcurrentCircularArray<E> implements Iterable<E> {
    private final Object[] list;
    private final int size;

    private final Lock lock = new ReentrantLock();
    private int index = 0;

    public ConcurrentCircularArray(int size) {
        this.list = new Object[size];
        this.size = size;
    }

    public void add(E item) {
        lock.lock();
        try {
            if (index == size) index = 0;
            list[index] = item;
            index++;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        lock.lock();
        try {
            return Iterators.transform(Iterators.forArray(list), e -> (E) e);
        } finally {
            lock.unlock();
        }
    }
}
