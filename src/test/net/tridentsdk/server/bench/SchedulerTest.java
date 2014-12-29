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
import net.tridentsdk.concurrent.TridentRunnable;
import net.tridentsdk.config.JsonConfig;
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.ConfigFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.plugin.annotation.PluginDescription;
import net.tridentsdk.server.TridentScheduler;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.util.TridentLogger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/*
Benchmark results: http://bit.ly/12fTNow
 */
@State(Scope.Benchmark) public class SchedulerTest {
    static {
        TridentLogger.init();
        Factories.init(new ConfigFactory() {
            @Override
            public JsonConfig serverConfig() {
                return new JsonConfig(Paths.get("/topkek"));
            }
        });
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
        Factories.init(TridentScheduler.create());
        Factories.init(new ThreadsHandler());
    }
    private static final TridentScheduler scheduler = TridentScheduler.create();

    //@Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
    private int cpuTokens;

    public static void main8(String... args) throws InterruptedException {
        @PluginDescription(name = "LOLCODE") class PluginImpl extends TridentPlugin {
        }

        for (int i = 0; i < 1000; i++) {
            scheduler.asyncRepeat(new PluginImpl(), new TridentRunnable() {
                @Override
                public void run() {
                    System.out.println("Your mom");
                }
            }, 0L, 42L);
        }

        for (int i = 0; i < 1000000; i++) {
            Thread.sleep(50);
            scheduler.tick();
        }
    }

    public static void main1(String... args) {
        TridentScheduler scheduler = TridentScheduler.create();
        while (true) {
            scheduler.tick();
        }
    }

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + SchedulerTest.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS).mode(Mode.AverageTime).warmupIterations(25)
                .warmupTime(TimeValue.milliseconds(20))             // ALLOWED TIME
                .measurementIterations(5).measurementTime(TimeValue.milliseconds(25))         // ALLOWED TIME
                .forks(1)                                           // FORKS
                //.verbosity(VerboseMode.SILENT)                      // GRAPH
                .threads(1)
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        scheduler.stop();
        Benchmarks.chart(Benchmarks.parse(results), "Scheduler+performance");
    }

    public static void main0(String... args) throws InterruptedException {
        TridentScheduler scheduler = TridentScheduler.create();
        for (int i = 0; i < 100; i++) {
            @PluginDescription(name = "LOLCODE") class PluginImpl extends TridentPlugin {
            }

            final int finalI = i;
            scheduler.asyncRepeat(new PluginImpl(), new TridentRunnable() {
                @Override
                public void run() {
                    System.out.println("LOL: " + finalI);
                }
            }, 0L, 1L);
        }
        for (int i = 0; i < 100; i++) {
            Thread.sleep(50);
            scheduler.tick();
        }

        scheduler.stop();
    }

    @Setup
    public void setup() {
        Factories.init(new ThreadsHandler());
        for (int i = 0; i < 100000; i++) {
            @PluginDescription(name = "LOLCODE") class PluginImpl extends TridentPlugin {
            }

            scheduler.asyncRepeat(new PluginImpl(), new TridentRunnable() {
                @Override
                public void run() {
                    System.out.print("");
                }
            }, 0L, 1L);
        }
    }

    //@Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void tick() {
        Blackhole.consumeCPU(cpuTokens);
        scheduler.tick();
    }
}
