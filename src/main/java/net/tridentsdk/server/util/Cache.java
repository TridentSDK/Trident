/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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

import net.tridentsdk.util.Tuple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A cache that expires stored instances after specified
 * timeout.
 *
 * @author TridentSDK
 * @since 0.5-alpha
 * @param <T> The key type of the cache
 * @param <M> The value type of the cache
 */
@ThreadSafe
public class Cache<T, M> {
    /**
     * Limiting value used to prevent eviction from taking
     * FOREVER.
     */
    private static final int MAX_EVICTION_ITERATIONS = 100;
    /**
     * A no-op removal listener used for evictions that
     * require no further action
     */
    private static final BiFunction NOP = (a, b) -> null;

    /**
     * The internal mapping of the cache entries
     */
    private final ConcurrentHashMap<T, Tuple<M, Long>> cache = new ConcurrentHashMap<>();
    /**
     * The amount of time, in millis, in which a cache entry
     * will timeout
     */
    private final long timeout;
    /**
     * The the expiry listener, which is notified whenever
     * an entry is evicted do to a timeout
     */
    private final BiFunction<T, M, Boolean> expire;

    /**
     * Builds a cache with the given number of milliseconds
     * before its entries timeout, performing no further
     * action upon entry eviction.
     *
     * @param timeout the amount of time in which cache
     * entries will timeout
     */
    public Cache(long timeout) {
        this(timeout, NOP);
    }

    /**
     * Constructs a cache in which entries expire within the
     * given timeout and perform the action given by the
     * expiry listener when evicted.
     *
     * @param timeout the amount of time in which the cache
     * entries will timeout
     * @param expire the expiry listener
     */
    public Cache(long timeout, BiFunction<T, M, Boolean> expire) {
        this.timeout = timeout;
        this.expire = expire;
    }

    /**
     * Obtains the value associated with the entry that has
     * the given key, or else inserts a new cache entry
     * with the given key and the value supplied by the
     * loader.
     *
     * @param key the key with which to find the cache entry
     * @param loader the computation which is run in order
     * to find the value if the entry does not exist
     * @return the cached or computed value
     */
    @Nonnull
    public M get(T key, Supplier<M> loader) {
        this.scan();
        Tuple<M, Long> instance = this.cache.get(key);

        // If the given value does not exist,
        // Perform computation (do not use raw insert
        // because computeIfAbsent will recover from races)
        // If the given value DOES exist,
        // If timed out, notify the listener (ensuring that
        // a remove works in order to prevent prescan races)
        // and compute
        // (again using compute to recover from races)
        // Otherwise, value exists and is valid, return it

        if (instance == null) {
            return this.cache.computeIfAbsent(key, t -> new Tuple<>(loader.get(), System.currentTimeMillis())).getA();
        } else {
            if (System.currentTimeMillis() - instance.getB() > this.timeout) {
                this.cache.computeIfPresent(key, (k, v) -> this.expire.apply(key, instance.getA()) ? null : instance);
                return this.cache.computeIfAbsent(key, t -> new Tuple<>(loader.get(), System.currentTimeMillis())).getA();
            }

            return instance.getA();
        }
    }

    /**
     * Computes the value at this cache entry, returning the
     * new value to put into or returning {@code null} to
     * remove it.
     *
     * @param key the key to check
     * @param loader the value loader
     * @return the value, or {@code null} if it is now or
     * was {@code null}
     */
    public M compute(T key, BiFunction<T, M, M> loader) {
        Tuple<M, Long> compute = this.cache.compute(key, (k, v) -> {
            if (v == null) {
                M apply = loader.apply(k, null);
                return new Tuple<>(apply, System.currentTimeMillis());
            } else {
                M apply = loader.apply(k, v.getA());
                return apply == null ? null : new Tuple<>(apply, System.currentTimeMillis());
            }
        });

        return compute == null ? null : compute.getA();
    }

    /**
     * Obtains the value of the cache entry with the given
     * key, or {@code null} if it does not exist, or if the
     * cache entry has already expired.
     *
     * @param key the key with which to find the value
     * @return the value associated with the given key, or
     * {@code null} if not present or expired
     */
    @Nullable
    public M get(T key) {
        this.scan();
        Tuple<M, Long> instance = this.cache.get(key);

        // If null, no entry contained. Return null.
        // Otherwise, check if expired
        // Notify listener, and use remove(key, value) to
        // recover from races (relies on all Tuples being
        // unique), ensure removal works before notifying
        // in case other threads prescan
        // Then return null to indicate no non-expired value
        // If valid and non-expired, all return the value

        if (instance == null) {
            return null;
        }

        if (System.currentTimeMillis() - instance.getB() > this.timeout) {
            Tuple<M, Long> tuple = this.cache.computeIfPresent(key, (k, v) -> this.expire.apply(key, instance.getA()) ? null : instance);
            return tuple == null ? null : tuple.getA();
        }

        return instance.getA();
    }

    /**
     * Inserts the given key and value pair into a cache
     * entry, regardless of whether the entry already
     * exists.
     *
     * @param key the key which to associate with the value
     * @param value the value which to associate with the
     * key
     */
    public void put(T key, M value) {
        this.scan();
        this.cache.put(key, new Tuple<>(value, System.currentTimeMillis()));
    }

    /**
     * Attempt to scan and remove cache entries that have
     * already expired.
     */
    private void scan() {
        long time = System.currentTimeMillis();

        int rounds = 0;
        for (Iterator<Map.Entry<T, Tuple<M, Long>>> it =
             this.cache.entrySet().
                stream().
                sorted(Comparator.comparingLong(o -> o.getValue().getB())).
                filter(e -> time - e.getValue().getB() > this.timeout).
                iterator();
             it.hasNext() && rounds < MAX_EVICTION_ITERATIONS;
             rounds++) {
            Map.Entry<T, Tuple<M, Long>> e = it.next();

            this.cache.computeIfPresent(e.getKey(), (k, v) -> this.expire.apply(e.getKey(), e.getValue().getA()) ? null : e.getValue());
        }
    }
}