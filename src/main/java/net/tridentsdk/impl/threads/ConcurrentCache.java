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
package net.tridentsdk.impl.threads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;

/**
 * Cache wrapping {@link java.util.concurrent.ConcurrentHashMap}
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author The TridentSDK Team
 */
public class ConcurrentCache<K, V> {
    private final ConcurrentMap<K, Future<V>> cache = new ConcurrentHashMap<>();

    /**
     * Retrieves the key in the cache, or adds the return value of the callable provided, run in the executor provided
     *
     * @param k        the key to retrieve the value from, or assign it to
     * @param callable the result of which to assign the key a value if the key is not in the cache
     * @param executor the executor the run the callable in
     * @return the return value of the callable
     */
    public V retrieve(K k, Callable<V> callable, ExecutorService executor) {
        while (true) {
            Future<V> future = this.cache.get(k);

            if (future == null) {
                Future<V> task = new FutureTask<>(callable);
                future = this.cache.putIfAbsent(k, task);

                if (future == null) future = executor.submit(callable);
            }

            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (CancellationException e) {
                this.cache.remove(k, future);
            }
        }
    }

    /**
     * The values of the cache
     *
     * @return the cache values
     */
    public Iterable<V> values() {
        Collection<V> list = new ArrayList<>();

        for (Future<V> v : this.cache.values()) {
            try {
                list.add(v.get());
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }

        return list;
    }

    /**
     * Removes the entry assigned to the specified key
     *
     * @param k the key to remove the entry for
     * @return the old value assigned to the key, otherwise, {@code null} if not in the cache
     */
    public V remove(K k) {
        while (true) {
            Future<V> future = this.cache.get(k);

            if (future == null) return null;

            this.cache.remove(k);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
