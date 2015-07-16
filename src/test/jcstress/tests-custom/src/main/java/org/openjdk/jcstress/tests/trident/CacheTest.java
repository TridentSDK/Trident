/*
 * Copyright (c) 2014, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.openjdk.jcstress.tests.trident;

import net.tridentsdk.concurrent.HeldValueLatch;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.util.TridentLogger;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.BooleanResult4;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

@JCStressTest
@Outcome(getID = "[true, true, true, false]", expect = Expect.ACCEPTABLE, desc = "Map works correctly")
@Outcome(expect = Expect.FORBIDDEN)
public class CacheTest {
    private final Object object = new Object();

    @Actor
    public void insertNullValue(ConcurrentCache<Object, Object> cache, BooleanResult4 result4) {
        Object object = new Object();
        try {
            cache.retrieve(object, () -> null);
        } catch (Exception e) {
            result4.r1 = true;
        }
    }

    @Actor
    public void insertNullKey(ConcurrentCache<Object, Object> cache, BooleanResult4 result4) {
        try {
            cache.retrieve(null, () -> object);
        } catch (Exception e) {
            result4.r2 = true;
        }
    }

    @Actor
    public void insertRemove(ConcurrentCache<Object, Object> cache, BooleanResult4 result4) {
        cache.retrieve(object, () -> object);
    }

    @Arbiter
    public void check(ConcurrentCache<Object, Object> cache, BooleanResult4 result4) {
        Object removed = cache.remove(object);
        if (removed == object && cache.remove(object) == null)
            result4.r3 = true;
    }

    @State
    public static class ConcurrentCache<K, V> {
        private final ConcurrentMap<K, HeldValueLatch<V>> cache = Factories.collect().createMap();

        private ConcurrentCache() {
        }

        /**
         * Creates a new cache
         *
         * @param <K> the key type
         * @param <V> the value type
         * @return a new cache
         */
        public static <K, V> ConcurrentCache<K, V> create() {
            return new ConcurrentCache<>();
        }

        /**
         * Retrieves the key in the cache, or adds the return value of the callable provided
         *
         * @param k        the key to retrieve the value from, or assign it to
         * @param callable the result of which to assign the key a value if the key is not in the cache
         * @return the return value of the callable
         */
        public V retrieve(K k, Callable<V> callable) {
            while (true) {
                HeldValueLatch<V> value = cache.get(k);
                if (value == null) {
                    HeldValueLatch<V> latch = HeldValueLatch.create();
                    value = cache.putIfAbsent(k, latch);
                    if (value == null) {
                        value = latch;
                        try {
                            value.countDown(callable.call());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    return value.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Removes the entry assigned to the specified key
         *
         * @param k the key to remove the entry for
         * @return the old value assigned to the key, otherwise, {@code null} if not in the cache
         */
        public V remove(K k) {
            HeldValueLatch<V> val = this.cache.get(k);

            if (val == null)
                return null;

            this.cache.remove(k);
            try {
                return val.await();
            } catch (InterruptedException e) {
                TridentLogger.error(e);
                return null;
            }
        }
    }
}
