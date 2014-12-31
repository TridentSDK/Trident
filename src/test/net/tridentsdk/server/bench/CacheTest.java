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

package net.tridentsdk.server.bench;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.concurrent.ConcurrentCache;
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.TridentScheduler;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.util.TridentLogger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/*
Benchmark results: http://bit.ly/1A21o5O
 */
@State(Scope.Benchmark) public class CacheTest {
    static {
        TridentLogger.init();
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
        Factories.init(ThreadsHandler.create());
        Factories.init(TridentScheduler.create());
    }

    private static final ConcurrentCache<Object, Object> CACHE = ConcurrentCache.create();
    private static final ConcurrentHashMap<Object, Object> CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    private static final Object key = new Object();
    private static final Callable<Object> CALLABLE = new Callable<Object>() {
        @Override
        public Object call() throws Exception {
            return "LOL";
        }
    };
    //@Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
    private int cpuTokens;

    public static void main0(String[] args) {
        CACHE.retrieve(key, CALLABLE);
        System.out.println(CACHE.remove(key));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + CacheTest.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS).mode(Mode.AverageTime).warmupIterations(20).warmupTime(
                        TimeValue.milliseconds(1))              // ALLOWED TIME
                .measurementIterations(5).measurementTime(TimeValue.milliseconds(1))         // ALLOWED TIME
                .forks(1)                                           // FORKS
                //.verbosity(VerboseMode.SILENT)                      // GRAPH
                .threads(4)                                         // THREADS
                .build();

        Benchmarks.chart(Benchmarks.parse(new Runner(opt).run()), "ConcurrentCache vs ConcurrentHashMap"); // TITLE
    }

    @Setup
    public void setUp() {
        CONCURRENT_HASH_MAP.put(key, CALLABLE);
    }

    //@Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void retrieve(Blackhole bh) {
        //Blackhole.consumeCPU(cpuTokens);
        bh.consume(CACHE.retrieve(key, CALLABLE));
    }

    //@Benchmark
    public void chmRetrieve(Blackhole bh) {
        //Blackhole.consumeCPU(cpuTokens);
        bh.consume(CONCURRENT_HASH_MAP.get(key));
    }
}
