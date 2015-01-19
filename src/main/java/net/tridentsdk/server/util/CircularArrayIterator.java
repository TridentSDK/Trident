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

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for {@link net.tridentsdk.server.util.ConcurrentCircularArray}
 *
 * <p>Removals are done on a best effort basis, and are not guaranteed to reflect the
 * same value at removal as they do when this object is created, use with caution.</p>
 *
 * <p>Should only be used by one thread at a time</p>
 */
@NotThreadSafe
public class CircularArrayIterator<E> implements Iterator<E> {

    private final ConcurrentCircularArray<E> parent;

    private final Object[] contents;
    private final int elements;

    // The current index to read from
    private int current;

    // The number of items read
    private int read;

    /**
     * Creates a new Iterator based on the ConcurrentCircularArray
     *
     * <p>Expects a read lock to be in place when constructing</p>
     */
    protected CircularArrayIterator(ConcurrentCircularArray<E> parent) {
        this.parent = parent;

        contents = new Object[parent.getMaxSize()];
        int notNulls = 0;
        for (int i = 0; i < parent.getMaxSize(); i++) {
            contents[i] = parent.backing.get(i);
            if (contents[i] != null) {
                notNulls++;
            }
        }

        elements = notNulls;
    }

    @Override
    public boolean hasNext() {
        return read != elements;
    }

    /**
     * @return null if an error, otherwise the next value
     */
    @Override
    public E next() throws NoSuchElementException {
        if (read >= elements) {
            throw new NoSuchElementException("Iterator has run out of items");
        }
        current++;
        if (current >= parent.getMaxSize()) {
            current = 0;
        }
        read++;
        // skips null values, effectively a while (value != null) loop with a limit to prevent infinite loops
        for (int i = 0; i < parent.getMaxSize(); i++) {
            if (contents[current] != null) {
                return (E) contents[current];
            }
            current++;
            if (current >= parent.getMaxSize()) {
                current = 0;
            }
        }

        return null;
    }

    @Override
    public void remove() {
        parent.remove(current, null);
    }

    /**
     * Guarantees that this value has not been changed since the iterator was created before removing the value
     */
    public boolean strictRemove() {
        return parent.remove(current, (E) contents[current]);
    }
}
