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

# Run progress: 0.00% complete, ETA 00:00:00
# Warmup: 25 iterations, 20 ms each
# Measurement: 5 iterations, 25 ms each
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.SchedulerTest.tick
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7532 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 82472397.000 ns/op
# Warmup Iteration   2: 19020582.000 ns/op
# Warmup Iteration   3: 72462438.700 ns/op
# Warmup Iteration   4: 20361509.167 ns/op
# Warmup Iteration   5: 19611381.429 ns/op
# Warmup Iteration   6: 19447276.857 ns/op
# Warmup Iteration   7: 10472224.833 ns/op
# Warmup Iteration   8: 15277658.750 ns/op
# Warmup Iteration   9: 12492846.400 ns/op
# Warmup Iteration  10: 17103343.375 ns/op
# Warmup Iteration  11: 40846311.571 ns/op
# Warmup Iteration  12: 19821168.571 ns/op
# Warmup Iteration  13: 21365621.333 ns/op
# Warmup Iteration  14: 21412102.833 ns/op
# Warmup Iteration  15: 13265385.100 ns/op
# Warmup Iteration  16: 15487724.250 ns/op
# Warmup Iteration  17: 18230014.714 ns/op
# Warmup Iteration  18: 7827911.813 ns/op
# Warmup Iteration  19: 14630731.889 ns/op
# Warmup Iteration  20: 19741763.714 ns/op
# Warmup Iteration  21: 17853731.000 ns/op
# Warmup Iteration  22: 30922404.667 ns/op
# Warmup Iteration  23: 20465000.833 ns/op
# Warmup Iteration  24: 20801157.167 ns/op
# Warmup Iteration  25: 20541897.500 ns/op
Iteration   1: 20477338.714 ns/op
Iteration   2: 17968266.143 ns/op
Iteration   3: 19406848.714 ns/op
Iteration   4: 19477014.571 ns/op
Iteration   5: 18614488.857 ns/op

Result: 19188791.400 ±(99.9%) 3658355.472 ns/op [Average]
  Statistics: (min, avg, max) = (17968266.143, 19188791.400, 20477338.714), stdev = 950063.298
  Confidence interval (99.9%): [15530435.928, 22847146.872]


# Run complete. Total time: 00:00:08

Benchmark                      Mode   Samples        Score  Score error    Units
n.t.s.b.SchedulerTest.tick     avgt         5 19188791.400  3658355.472    ns/op

//////////////VERSUS Bukkit Scheduler, equivalent task, task count, task frequency, task delay /////////////////////////
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Warmup: 20 iterations, 25 ms each
# Measurement: 5 iterations, 25 ms each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: com.gmail.woodyc40.benchmarks.Collective.tickBukkit

# Run progress: 0.00% complete, ETA 00:00:00
# Fork: 1 of 1
# Warmup Iteration   1: 2629727812.000 ns/op
# Warmup Iteration   2: 556080820.000 ns/op
# Warmup Iteration   3: 384411310.000 ns/op
# Warmup Iteration   4: 627199301.000 ns/op
# Warmup Iteration   5: 396606988.000 ns/op
# Warmup Iteration   6: 2443624771.000 ns/op
# Warmup Iteration   7: 176844997.000 ns/op
# Warmup Iteration   8: 170143216.000 ns/op
# Warmup Iteration   9: 181664868.000 ns/op
# Warmup Iteration  10: 198237425.000 ns/op
# Warmup Iteration  11: 635635643.000 ns/op
# Warmup Iteration  12: 187161037.000 ns/op
# Warmup Iteration  13: 443887824.000 ns/op
# Warmup Iteration  14: 181496926.000 ns/op
# Warmup Iteration  15: 209264071.000 ns/op
# Warmup Iteration  16: 406736510.000 ns/op
# Warmup Iteration  17: 202293280.000 ns/op
# Warmup Iteration  18: 2767499650.000 ns/op
# Warmup Iteration  19: 186283247.000 ns/op
# Warmup Iteration  20: 166186605.000 ns/op
Iteration   1: 543949216.000 ns/op
Iteration   2: 173408271.000 ns/op
Iteration   3: 173201565.000 ns/op
Iteration   4: 216850860.000 ns/op
Iteration   5: 707540626.000 ns/op

Trident is 10x faster than Bukkit
 */
@State(Scope.Benchmark) public class SchedulerTest {
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
        Factories.init(ThreadsHandler.create());
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
