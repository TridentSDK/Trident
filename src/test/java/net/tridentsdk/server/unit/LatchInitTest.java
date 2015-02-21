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
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.TridentTaskScheduler;
import net.tridentsdk.server.threads.ThreadsHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ConcurrentMap;

public class LatchInitTest extends AbstractTest {
    @Rule
    public ConcurrentRule concurrently = new ConcurrentRule();
    @Rule
    public RepeatingRule repeatedly = new RepeatingRule();

    @Test
    @Concurrent(count = 16)
    @Repeating(repetition = 5)
    public void doTest() {
        assertNotNull(Factories.collect());
        assertNotNull(Factories.threads());
        assertNotNull(Factories.tasks());
        assertNotNull(Factories.configs());
    }

    @Before
    public void setup() {
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
        Factories.init(TridentTaskScheduler.create());
        Factories.init(ThreadsHandler.create());
    }
}
