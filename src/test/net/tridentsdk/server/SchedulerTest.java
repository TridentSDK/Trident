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

package net.tridentsdk.server;

import net.tridentsdk.concurrent.TridentRunnable;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.plugin.annotation.PluginDescription;
import net.tridentsdk.server.threads.ThreadsManager;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/*
Benchmark results: http://bit.ly/12fTNow
 */
@State(Scope.Benchmark)
public class SchedulerTest {
    private static final TridentScheduler scheduler = new TridentScheduler();

    @Setup
    public void setup() {
        Factories.init(new ThreadsManager());
        for (int i = 0; i < 100000; i++) {
            @PluginDescription(name = "LOLCODE")
            class PluginImpl extends TridentPlugin {
            }

            scheduler.asyncLater(new PluginImpl(), new TridentRunnable() {
                @Override
                public void run() {
                    System.out.print("");
                }
            }, 1L);
        }
    }

    public static void main8(String... args) throws InterruptedException {
        @PluginDescription(name = "LOLCODE")
        class PluginImpl extends TridentPlugin {
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
        TridentScheduler scheduler = new TridentScheduler();
        while (true) {
            scheduler.tick();
        }
    }

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + SchedulerTest.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(20)
                .warmupTime(TimeValue.milliseconds(20))              // ALLOWED TIME
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(20))         // ALLOWED TIME
                .forks(1)                                           // FORKS
                .verbosity(VerboseMode.SILENT)                      // GRAPH
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        scheduler.stop();
        Benchmarks.chart(Benchmarks.parse(results), "Scheduler+performance");
    }

    @Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024"})
    private int cpuTokens;

    @Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void tick() {
        Blackhole.consumeCPU(cpuTokens);
        scheduler.tick();
    }

    public static void main0(String... args) throws InterruptedException {
        TridentScheduler scheduler = new TridentScheduler();
        for (int i = 0; i < 100; i++) {
            @PluginDescription(name = "LOLCODE")
            class PluginImpl extends TridentPlugin {
            }

            final int finalI = i;
            scheduler.asyncRepeat(new PluginImpl(), new TridentRunnable() {
                @Override
                public void run() {
                    System.out.println("LOL: " + finalI);
                }
            }, 0L, 20L);
        }
        for (int i = 0; i < 100; i++) {
            Thread.sleep(50);
            scheduler.tick();
        }

        scheduler.stop();
    }
}
