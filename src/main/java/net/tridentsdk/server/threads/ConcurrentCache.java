/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server.threads;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Cache wrapping {@link java.util.concurrent.ConcurrentHashMap}
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author The TridentSDK Team
 */
public class ConcurrentCache<K, V> {
    private final ConcurrentMap<K, Future<V>> cache = new ConcurrentHashMapV8<>();

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
