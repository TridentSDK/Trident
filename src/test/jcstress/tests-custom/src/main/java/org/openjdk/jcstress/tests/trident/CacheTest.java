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

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.BooleanResult4;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

@JCStressTest
@Outcome(id = "[true, true, true, false]", expect = Expect.ACCEPTABLE, desc = "Map works correctly")
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
        private static final Object PLACE_HOLDER = new Object();
        private final ConcurrentMap<K, Object> cache = new ConcurrentHashMapV8<>();

        /**
         * Retrieves the key in the cache, or adds the return value of the callable provided
         *
         * @param k        the key to retrieve the value from, or assign it to
         * @param callable the result of which to assign the key a value if the key is not in the cache
         * @return the return value of the callable
         */
        public V retrieve(K k, Callable<V> callable) {
            Object value = cache.get(k);
            if (value == null) {
                V v = null;
                value = cache.putIfAbsent(k, PLACE_HOLDER);
                if (value == null) {
                    try {
                        v = callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    cache.replace(k, PLACE_HOLDER, v);
                    value = v;
                }
            }

            return (V) value;
        }

        public Set<K> keys() {
            return this.cache.keySet();
        }

        /**
         * The values of the cache
         *
         * @return the cache values
         */
        public Collection<V> values() {
            Collection<V> list = new ArrayList<>();
            for (Object v : this.cache.values())
                list.add((V) v);

            return list;
        }

        /**
         * Removes the entry assigned to the specified key
         *
         * @param k the key to remove the entry for
         * @return the old value assigned to the key, otherwise, {@code null} if not in the cache
         */
        public V remove(K k) {
            Object val = this.cache.get(k);

            if (val == null)
                return null;

            this.cache.remove(k);
            return (V) val;
        }

        /**
         * Returns the backing map of this cache
         *
         * @return the underlying map
         */
        public Set<Map.Entry<K, V>> entries() {
            Set<Map.Entry<K, V>> entries = new HashSet<>();
            for (Map.Entry<K, Object> entry : cache.entrySet())
                entries.add(new AbstractMap.SimpleEntry<>(entry.getKey(), (V) entry.getValue()));
            return entries;
        }
    }
}
