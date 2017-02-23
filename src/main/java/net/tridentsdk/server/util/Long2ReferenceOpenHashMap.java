/*
 * Copyright (C) 2002-2016 Sebastiano Vigna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.util;

import net.tridentsdk.doc.Policy;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

/**
 * Minimal class of the one included in fastutil, Vilsol got
 * salty about 40MB dependency and wouldn't shade it lmao.
 *
 * Summary of changes made:
 * - If its deprecated its removed
 * - If its never used its removed
 * - If its an error remove the whole thing
 * - Change all the default ret values
 * - If it comes from another class put it first
 * - If it extends another class remove whatever it extends
 * - Otherwise its copy pasted
 * - This class isn't ours! Check the license header
 */
@Policy("license change")
@NotThreadSafe
public class Long2ReferenceOpenHashMap<V> {
    /**
     * Return the least power of two greater than or equal
     * to the specified value.
     *
     * <p>Note that this function will return 1 when the
     * argument is 0.
     *
     * @param x a long integer smaller than or equal to
     * 2<sup>62</sup>.
     * @return the least power of two greater than or equal
     * to the specified value.
     */
    public static long nextPowerOfTwo(long x) {
        if (x == 0) return 1;
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return (x | x >> 32) + 1;
    }

    /**
     * 2<sup>64</sup> &middot; &phi;, &phi; = (&#x221A;5
     * &minus; 1)/2.
     */
    private static final long LONG_PHI = 0x9E3779B97F4A7C15L;

    public final static long mix(final long x) {
        long h = x * LONG_PHI;
        h ^= h >>> 32;
        return h ^ (h >>> 16);
    }

    /**
     * Returns the maximum number of entries that can be
     * filled before rehashing.
     *
     * @param n the size of the backing array.
     * @param f the load factor.
     * @return the maximum number of entries before
     * rehashing.
     */
    public static int maxFill(final int n, final float f) {
        /* We must guarantee that there is always at least
		 * one free entry (even with pathological load factors). */
        return Math.min((int) Math.ceil(n * f), n - 1);
    }

    /**
     * Returns the least power of two smaller than or equal
     * to 2<sup>30</sup> and larger than or equal to
     * <code>Math.ceil( expected / f )</code>.
     *
     * @param expected the expected number of elements in a
     * hash table.
     * @param f the load factor.
     * @return the minimum possible size for a backing
     * array.
     * @throws IllegalArgumentException if the necessary
     * size is larger than 2<sup>30</sup>.
     */
    public static int arraySize(final int expected, final float f) {
        final long s = Math.max(2, nextPowerOfTwo((long) Math.ceil(expected / f)));
        if (s > (1 << 30))
            throw new IllegalArgumentException("Too large (" + expected + " expected elements with load factor " + f + ")");
        return (int) s;
    }

    /**
     * The initial default size of a hash table.
     */
    public static final int DEFAULT_INITIAL_SIZE = 16;
    /**
     * The default load factor of a hash table.
     */
    public static final float DEFAULT_LOAD_FACTOR = .75f;

    public static class LongArrayList {
        /**
         * Ensures that the given index is nonnegative and
         * not greater than the list
         * size.
         *
         * @param index an index.
         * @throws IndexOutOfBoundsException if the given
         * index is negative or greater than the list size.
         */
        protected void ensureIndex(final int index) {
            if (index < 0)
                throw new IndexOutOfBoundsException("Index (" + index
                        + ") is negative");
            if (index > this.size())
                throw new IndexOutOfBoundsException("Index (" + index
                        + ") is greater than list size (" + (this.size()) + ")");
        }

        /**
         * Ensures that an array can contain the given
         * number of entries, preserving
         * just a part of the array.
         *
         * @param array an array.
         * @param length the new minimum length for this
         * array.
         * @param preserve the number of elements of the
         * array that must be preserved in
         * case a new allocation is necessary.
         * @return <code>array</code>, if it can contain
         * <code>length</code> entries
         * or more; otherwise, an array with
         * <code>length</code> entries
         * whose first <code>preserve</code> entries are the
         * same as those
         * of <code>array</code>.
         */
        public static long[] ensureCapacity(final long[] array, final int length,
                                            final int preserve) {
            if (length > array.length) {
                final long t[] = new long[length];
                System.arraycopy(array, 0, t, 0, preserve);
                return t;
            }
            return array;
        }

        /**
         * This is a safe value used by {@link ArrayList}
         * (as of Java 7) to avoid
         * throwing {@link OutOfMemoryError} on some JVMs.
         * We adopt the same value.
         */
        public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

        /**
         * Grows the given array to the maximum between the
         * given length and the
         * current length multiplied by two, provided that
         * the given length is
         * larger than the current length, preserving just a
         * part of the array.
         *
         * <P>
         * If you want complete control on the array growth,
         * you should probably use
         * <code>ensureCapacity()</code> instead.
         *
         * @param array an array.
         * @param length the new minimum length for this
         * array.
         * @param preserve the number of elements of the
         * array that must be preserved in
         * case a new allocation is necessary.
         * @return <code>array</code>, if it can contain
         * <code>length</code>
         * entries; otherwise, an array with
         * max(<code>length</code>,
         * <code>array.length</code>/&phi;) entries whose
         * first
         * <code>preserve</code> entries are the same as
         * those of
         * <code>array</code>.
         */
        public static long[] grow(final long[] array, final int length,
                                  final int preserve) {
            if (length > array.length) {
                final int newLength = (int) Math.max(
                        Math.min(2L * array.length, MAX_ARRAY_SIZE), length);
                final long t[] = new long[newLength];
                System.arraycopy(array, 0, t, 0, preserve);
                return t;
            }
            return array;
        }

        /**
         * The backing array.
         */
        protected transient long a[];
        /**
         * The current actual size of the list (never
         * greater than the backing-array
         * length).
         */
        protected int size;
        private static final boolean ASSERTS = false;

        /**
         * Creates a new array list using a given array.
         *
         * <P>
         * This constructor is only meant to be used by the
         * wrapping methods.
         *
         * @param a the array that will be used to back this
         * array list.
         */
        @SuppressWarnings("unused")
        protected LongArrayList(final long a[], boolean dummy) {
            this.a = a;
        }

        /**
         * Creates a new array list with given capacity.
         *
         * @param capacity the initial capacity of the array
         * list (may be 0).
         */

        public LongArrayList(final int capacity) {
            if (capacity < 0)
                throw new IllegalArgumentException("Initial capacity (" + capacity
                        + ") is negative");
            this.a = new long[capacity];
        }

        /**
         * Ensures that this array list can contain the
         * given number of entries
         * without resizing.
         *
         * @param capacity the new minimum capacity for this
         * array list.
         */
        public void ensureCapacity(final int capacity) {
            this.a = ensureCapacity(this.a, capacity, this.size);
            if (ASSERTS)
                assert this.size <= this.a.length;
        }

        /**
         * Grows this array list, ensuring that it can
         * contain the given number of
         * entries without resizing, and in case enlarging
         * it at least by a factor
         * of two.
         *
         * @param capacity the new minimum capacity for this
         * array list.
         */

        private void grow(final int capacity) {
            this.a = grow(this.a, capacity, this.size);
            if (ASSERTS)
                assert this.size <= this.a.length;
        }

        public void add(final int index, final long k) {
            this.ensureIndex(index);
            this.grow(this.size + 1);
            if (index != this.size)
                System.arraycopy(this.a, index, this.a, index + 1, this.size - index);
            this.a[index] = k;
            this.size++;
            if (ASSERTS)
                assert this.size <= this.a.length;
        }

        public boolean add(final long k) {
            this.grow(this.size + 1);
            this.a[this.size++] = k;
            if (ASSERTS)
                assert this.size <= this.a.length;
            return true;
        }

        public long getLong(final int index) {
            if (index >= this.size)
                throw new IndexOutOfBoundsException("Index (" + index
                        + ") is greater than or equal to list size (" + this.size + ")");
            return this.a[index];
        }

        public int indexOf(final long k) {
            for (int i = 0; i < this.size; i++)
                if (((k) == (this.a[i])))
                    return i;
            return -1;
        }

        public int lastIndexOf(final long k) {
            for (int i = this.size; i-- != 0; )
                if (((k) == (this.a[i])))
                    return i;
            return -1;
        }

        public long removeLong(final int index) {
            if (index >= this.size)
                throw new IndexOutOfBoundsException("Index (" + index
                        + ") is greater than or equal to list size (" + this.size + ")");
            final long old = this.a[index];
            this.size--;
            if (index != this.size)
                System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
            if (ASSERTS)
                assert this.size <= this.a.length;
            return old;
        }

        public boolean rem(final long k) {
            int index = this.indexOf(k);
            if (index == -1)
                return false;
            this.removeLong(index);
            if (ASSERTS)
                assert this.size <= this.a.length;
            return true;
        }

        public long set(final int index, final long k) {
            if (index >= this.size)
                throw new IndexOutOfBoundsException("Index (" + index
                        + ") is greater than or equal to list size (" + this.size + ")");
            long old = this.a[index];
            this.a[index] = k;
            return old;
        }

        public void clear() {
            this.size = 0;
            if (ASSERTS)
                assert this.size <= this.a.length;
        }

        public int size() {
            return this.size;
        }

        public void size(final int size) {
            if (size > this.a.length)
                this.ensureCapacity(size);
            if (size > this.size)
                Arrays.fill(this.a, this.size, size, (0));
            this.size = size;
        }
    }

    /**
     * The array of keys.
     */
    protected transient long[] key;
    /**
     * The array of values.
     */
    protected transient V[] value;
    /**
     * The mask for wrapping a position counter.
     */
    protected transient int mask;
    /**
     * Whether this set contains the key zero.
     */
    protected transient boolean containsNullKey;
    /**
     * The current table size.
     */
    protected transient int n;
    /**
     * Threshold after which we rehash. It must be the table
     * size times
     * {@link #f}.
     */
    protected transient int maxFill;
    /**
     * Number of entries in the set (including the key zero,
     * if present).
     */
    protected int size;
    /**
     * The acceptable load factor.
     */
    protected final float f;
    /**
     * Cached collection of values.
     */
    protected transient Collection<V> values;

    /**
     * Creates a new hash map.
     *
     * <p>
     * The actual table size will be the least power of two
     * greater than
     * <code>expected</code>/<code>f</code>.
     *
     * @param expected the expected number of elements in
     * the hash set.
     * @param f the load factor.
     */
    @SuppressWarnings("unchecked")
    public Long2ReferenceOpenHashMap(final int expected, final float f) {
        if (f <= 0 || f > 1)
            throw new IllegalArgumentException(
                    "Load factor must be greater than 0 and smaller than or equal to 1");
        if (expected < 0)
            throw new IllegalArgumentException(
                    "The expected number of elements must be nonnegative");
        this.f = f;
        this.n = arraySize(expected, f);
        this.mask = this.n - 1;
        this.maxFill = maxFill(this.n, f);
        this.key = new long[this.n + 1];
        this.value = (V[]) new Object[this.n + 1];
    }

    public Long2ReferenceOpenHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    private int realSize() {
        return this.containsNullKey ? this.size - 1 : this.size;
    }

    private V removeEntry(final int pos) {
        final V oldValue = this.value[pos];
        this.value[pos] = null;
        this.size--;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > DEFAULT_INITIAL_SIZE)
            this.rehash(this.n / 2);
        return oldValue;
    }

    private V removeNullEntry() {
        this.containsNullKey = false;
        final V oldValue = this.value[this.n];
        this.value[this.n] = null;
        this.size--;
        if (this.size < this.maxFill / 4 && this.n > DEFAULT_INITIAL_SIZE)
            this.rehash(this.n / 2);
        return oldValue;
    }

    private int insert(final long k, final V v) {
        int pos;
        if (((k) == (0))) {
            if (this.containsNullKey)
                return this.n;
            this.containsNullKey = true;
            pos = this.n;
        } else {
            long curr;
            final long[] key = this.key;
            // The starting point.
            if (!((curr = key[pos = (int) mix((k)) & this.mask]) == (0))) {
                if (((curr) == (k)))
                    return pos;
                while (!((curr = key[pos = (pos + 1) & this.mask]) == (0)))
                    if (((curr) == (k)))
                        return pos;
            }
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size++ >= this.maxFill)
            this.rehash(arraySize(this.size + 1, this.f));
        return -1;
    }

    public V put(final long k, final V v) {
        final int pos = this.insert(k, v);
        if (pos < 0)
            return null;
        final V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    /**
     * Shifts left entries with the specified hash code,
     * starting at the
     * specified position, and empties the resulting free
     * entry.
     *
     * @param pos a starting position.
     */
    protected final void shiftKeys(int pos) {
        // Shift entries with the same hash.
        int last, slot;
        long curr;
        final long[] key = this.key;
        for (; ; ) {
            pos = ((last = pos) + 1) & this.mask;
            for (; ; ) {
                if (((curr = key[pos]) == (0))) {
                    key[last] = (0);
                    this.value[last] = null;
                    return;
                }
                slot = (int) mix((curr))
                        & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot
                        && slot > pos)
                    break;
                pos = (pos + 1) & this.mask;
            }
            key[last] = curr;
            this.value[last] = this.value[pos];
        }
    }

    public V remove(final long k) {
        if (((k) == (0))) {
            if (this.containsNullKey)
                return this.removeNullEntry();
            return null;
        }
        long curr;
        final long[] key = this.key;
        int pos;
        // The starting point.
        if (((curr = key[pos = (int) mix((k))
                & this.mask]) == (0)))
            return null;
        if (((k) == (curr)))
            return this.removeEntry(pos);
        while (true) {
            if (((curr = key[pos = (pos + 1) & this.mask]) == (0)))
                return null;
            if (((k) == (curr)))
                return this.removeEntry(pos);
        }
    }

    public V get(final long k) {
        if (((k) == (0)))
            return this.containsNullKey ? this.value[this.n] : null;
        long curr;
        final long[] key = this.key;
        int pos;
        // The starting point.
        if (((curr = key[pos = (int) mix((k))
                & this.mask]) == (0)))
            return null;
        if (((k) == (curr)))
            return this.value[pos];
        // There's always an unused entry.
        while (true) {
            if (((curr = key[pos = (pos + 1) & this.mask]) == (0)))
                return null;
            if (((k) == (curr)))
                return this.value[pos];
        }
    }

    /*
     * Removes all elements from this map.
     *
     * <P>To increase object reuse, this method does not change the table size.
     * If you want to reduce the table size, you must use {@link #trim()}.
     */
    public void clear() {
        if (this.size == 0)
            return;
        this.size = 0;
        this.containsNullKey = false;
        Arrays.fill(this.key, (0));
        Arrays.fill(this.value, null);
    }

    public boolean containsKey(final long k) {
        if (((k) == (0)))
            return this.containsNullKey;
        long curr;
        final long[] key = this.key;
        int pos;
        // The starting point.
        if (((curr = key[pos = (int) mix((k))
                & this.mask]) == (0)))
            return false;
        if (((k) == (curr)))
            return true;
        // There's always an unused entry.
        while (true) {
            if (((curr = key[pos = (pos + 1) & this.mask]) == (0)))
                return false;
            if (((k) == (curr)))
                return true;
        }
    }

    public boolean containsValue(final Object v) {
        final V value[] = this.value;
        final long key[] = this.key;
        if (this.containsNullKey && ((value[this.n]) == (v)))
            return true;
        for (int i = this.n; i-- != 0; )
            if (!((key[i]) == (0)) && ((value[i]) == (v)))
                return true;
        return false;
    }

    /**
     * An iterator over a hash map.
     */
    private class MapIterator {
        /**
         * The index of the last entry returned, if positive
         * or zero; initially,
         * {@link #n}. If negative, the last entry returned
         * was that of the key
         * of index {@code - pos - 1} from the {@link
         * #wrapped} list.
         */
        int pos = Long2ReferenceOpenHashMap.this.n;
        /**
         * The index of the last entry that has been
         * returned (more precisely,
         * the value of {@link #pos} if {@link #pos} is
         * positive, or
         * {@link Integer#MIN_VALUE} if {@link #pos} is
         * negative). It is -1 if
         * either we did not return an entry yet, or the
         * last returned entry has
         * been removed.
         */
        int last = -1;
        /**
         * A downward counter measuring how many entries
         * must still be returned.
         */
        int c = Long2ReferenceOpenHashMap.this.size;
        /**
         * A boolean telling us whether we should return the
         * entry with the null
         * key.
         */
        boolean mustReturnNullKey = Long2ReferenceOpenHashMap.this.containsNullKey;
        /**
         * A lazily allocated list containing keys of
         * entries that have wrapped
         * around the table because of removals.
         */
        LongArrayList wrapped;

        public boolean hasNext() {
            return this.c != 0;
        }

        public int nextEntry() {
            if (!this.hasNext())
                throw new NoSuchElementException();
            this.c--;
            if (this.mustReturnNullKey) {
                this.mustReturnNullKey = false;
                return this.last = Long2ReferenceOpenHashMap.this.n;
            }
            final long key[] = Long2ReferenceOpenHashMap.this.key;
            for (; ; ) {
                if (--this.pos < 0) {
                    // We are just enumerating elements from the wrapped list.
                    this.last = Integer.MIN_VALUE;
                    final long k = this.wrapped.getLong(-this.pos - 1);
                    int p = (int) mix((k))
                            & Long2ReferenceOpenHashMap.this.mask;
                    while (!((k) == (key[p])))
                        p = (p + 1) & Long2ReferenceOpenHashMap.this.mask;
                    return p;
                }
                if (!((key[this.pos]) == (0)))
                    return this.last = this.pos;
            }
        }

        /**
         * Shifts left entries with the specified hash code,
         * starting at the
         * specified position, and empties the resulting
         * free entry.
         *
         * @param pos a starting position.
         */
        private final void shiftKeys(int pos) {
            // Shift entries with the same hash.
            int last, slot;
            long curr;
            final long[] key = Long2ReferenceOpenHashMap.this.key;
            for (; ; ) {
                pos = ((last = pos) + 1) & Long2ReferenceOpenHashMap.this.mask;
                for (; ; ) {
                    if (((curr = key[pos]) == (0))) {
                        key[last] = (0);
                        Long2ReferenceOpenHashMap.this.value[last] = null;
                        return;
                    }
                    slot = (int) mix((curr))
                            & Long2ReferenceOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot
                            && slot > pos)
                        break;
                    pos = (pos + 1) & Long2ReferenceOpenHashMap.this.mask;
                }
                if (pos < last) { // Wrapped entry.
                    if (this.wrapped == null)
                        this.wrapped = new LongArrayList(2);
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Long2ReferenceOpenHashMap.this.value[last] = Long2ReferenceOpenHashMap.this.value[pos];
            }
        }

        public void remove() {
            if (this.last == -1)
                throw new IllegalStateException();
            if (this.last == Long2ReferenceOpenHashMap.this.n) {
                Long2ReferenceOpenHashMap.this.containsNullKey = false;
                Long2ReferenceOpenHashMap.this.value[Long2ReferenceOpenHashMap.this.n] = null;
            } else if (this.pos >= 0)
                this.shiftKeys(this.last);
            else {
                // We're removing wrapped entries.
                Long2ReferenceOpenHashMap.this
                        .remove(this.wrapped.getLong(-this.pos - 1));
                this.last = -1; // Note that we must not decrement size
                return;
            }
            Long2ReferenceOpenHashMap.this.size--;
            this.last = -1; // You can no longer remove this entry.
        }

        public int skip(final int n) {
            int i = n;
            while (i-- != 0 && this.hasNext())
                this.nextEntry();
            return n - i - 1;
        }
    }

    /**
     * An iterator on values.
     *
     * <P>
     * We simply override the {@link java.util.ListIterator#next()}/
     * {@link java.util.ListIterator#previous()} methods
     * (and possibly their
     * type-specific counterparts) so that they return
     * values instead of
     * entries.
     */
    private final class ValueIterator extends MapIterator implements Iterator<V> {
        public ValueIterator() {
            super();
        }

        public V next() {
            return Long2ReferenceOpenHashMap.this.value[this.nextEntry()];
        }
    }

    public Collection<V> values() {
        if (this.values == null)
            this.values = new AbstractCollection<V>() {
                public Iterator<V> iterator() {
                    return new Long2ReferenceOpenHashMap.ValueIterator();
                }

                public int size() {
                    return Long2ReferenceOpenHashMap.this.size;
                }

                public boolean contains(Object v) {
                    return Long2ReferenceOpenHashMap.this.containsValue(v);
                }

                public void clear() {
                    Long2ReferenceOpenHashMap.this.clear();
                }
            };
        return this.values;
    }

    @Deprecated
    public boolean rehash() {
        return true;
    }

    /**
     * Rehashes the map.
     *
     * <P>
     * This method implements the basic rehashing strategy,
     * and may be overriden
     * by subclasses implementing different rehashing
     * strategies (e.g.,
     * disk-based rehashing). However, you should not
     * override this method
     * unless you understand the internal workings of this
     * class.
     *
     * @param newN the new size
     */
    @SuppressWarnings("unchecked")
    protected void rehash(final int newN) {
        final long key[] = this.key;
        final V value[] = this.value;
        final int mask = newN - 1; // Note that this is used by the hashing
        // macro
        final long newKey[] = new long[newN + 1];
        final V newValue[] = (V[]) new Object[newN + 1];
        int i = this.n, pos;
        for (int j = this.realSize(); j-- != 0; ) {
            while (((key[--i]) == (0))) ;
            if (!((newKey[pos = (int) mix((key[i])) & mask]) == (0)))
                while (!((newKey[pos = (pos + 1) & mask]) == (0)))
                    ;
            newKey[pos] = key[i];
            newValue[pos] = value[i];
        }
        newValue[newN] = value[this.n];
        this.n = newN;
        this.mask = mask;
        this.maxFill = maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }
}