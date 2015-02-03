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

package net.tridentsdk.server.unit;

import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.concurrent.ConcurrentCache;
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.TridentScheduler;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.util.TridentLogger;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

public class CacheTest extends AbstractTest {
    static {
        TridentLogger.init();
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
        Factories.init(TridentScheduler.create());
        Factories.init(ThreadsHandler.create());
    }

    private final ConcurrentCache<Object, Object> cache = ConcurrentCache.create();
    @Rule
    public ConcurrentRule concurrently = new ConcurrentRule();
    @Rule
    public RepeatingRule repeatedly = new RepeatingRule();

    @Test
    @Concurrent(count = 16)
    @Repeating(repetition = 500)
    public void insert() {
        final Object object = new Object();
        cache.retrieve(object, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return object;
            }
        });
        assertEquals(cache.remove(object), object);
    }

    @Test
    @Concurrent(count = 16)
    @Repeating(repetition = 500)
    public void insertNullValue() {
        final Object object = new Object();
        cache.retrieve(object, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        });
        assertNull(cache.remove(object));
    }

    @Test(expected = NullPointerException.class)
    @Concurrent(count = 16)
    @Repeating(repetition = 500)
    public void insertNullKey() {
        cache.retrieve(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        });
    }

    @Test
    @Concurrent(count = 16)
    @Repeating(repetition = 500)
    public void insertRemove() {
        final Object object = new Object();
        cache.retrieve(object, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return object;
            }
        });

        assertEquals(cache.remove(object), object);
        assertNull(cache.remove(object));
    }
}
