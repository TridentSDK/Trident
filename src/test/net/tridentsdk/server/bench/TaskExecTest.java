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
import net.tridentsdk.concurrent.TaskExecutor;
import net.tridentsdk.config.JsonConfig;
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.ConfigFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.TridentScheduler;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.util.TridentLogger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
Thread-1
Thread-3
Thread-0
Thread-2

Process finished with exit code 0
 */

/*
# Run progress: 0.00% complete, ETA 00:00:01
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.concurrentTaskExecutor
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7537 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 207.164 ns/op
# Warmup Iteration   2: 154.278 ns/op
# Warmup Iteration   3: 96.681 ns/op
# Warmup Iteration   4: 129.152 ns/op
# Warmup Iteration   5: 104.986 ns/op
# Warmup Iteration   6: 118.926 ns/op
# Warmup Iteration   7: 97.003 ns/op
# Warmup Iteration   8: 115.579 ns/op
# Warmup Iteration   9: 102.272 ns/op
# Warmup Iteration  10: 108.838 ns/op
# Warmup Iteration  11: 129.324 ns/op
# Warmup Iteration  12: 92.179 ns/op
# Warmup Iteration  13: 109.982 ns/op
# Warmup Iteration  14: 120.404 ns/op
# Warmup Iteration  15: 133.804 ns/op
# Warmup Iteration  16: 113.244 ns/op
# Warmup Iteration  17: 85.550 ns/op
# Warmup Iteration  18: 109.431 ns/op
# Warmup Iteration  19: 98.615 ns/op
# Warmup Iteration  20: 150.276 ns/op
Iteration   1: 105.056 ns/op
Iteration   2: 109.963 ns/op
Iteration   3: 135.858 ns/op
Iteration   4: 112.757 ns/op
Iteration   5: 105.579 ns/op
Iteration   6: 133.254 ns/op
Iteration   7: 112.914 ns/op
Iteration   8: 103.465 ns/op
Iteration   9: 102.590 ns/op
Iteration  10: 99.780 ns/op
Iteration  11: 140.375 ns/op
Iteration  12: 104.491 ns/op
Iteration  13: 111.051 ns/op
Iteration  14: 110.679 ns/op
Iteration  15: 104.706 ns/op
Iteration  16: 108.657 ns/op
Iteration  17: 121.909 ns/op
Iteration  18: 123.248 ns/op
Iteration  19: 103.666 ns/op
Iteration  20: 111.914 ns/op

Result: 113.096 ±(99.9%) 10.208 ns/op [Average]
  Statistics: (min, avg, max) = (99.780, 113.096, 140.375), stdev = 11.755
  Confidence interval (99.9%): [102.888, 123.303]


# Run progress: 33.33% complete, ETA 00:00:18
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.executorFactory
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7537 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 323.910 ns/op
# Warmup Iteration   2: 246.520 ns/op
# Warmup Iteration   3: 286.119 ns/op
# Warmup Iteration   4: 204.610 ns/op
# Warmup Iteration   5: 227.119 ns/op
# Warmup Iteration   6: 175.432 ns/op
# Warmup Iteration   7: 183.760 ns/op
# Warmup Iteration   8: 155.210 ns/op
# Warmup Iteration   9: 155.411 ns/op
# Warmup Iteration  10: 175.836 ns/op
# Warmup Iteration  11: 175.784 ns/op
# Warmup Iteration  12: 197.352 ns/op
# Warmup Iteration  13: 178.704 ns/op
# Warmup Iteration  14: 168.091 ns/op
# Warmup Iteration  15: 252.240 ns/op
# Warmup Iteration  16: 195.795 ns/op
# Warmup Iteration  17: 162.750 ns/op
# Warmup Iteration  18: 162.108 ns/op
# Warmup Iteration  19: 149.374 ns/op
# Warmup Iteration  20: 172.774 ns/op
Iteration   1: 179.904 ns/op
Iteration   2: 171.165 ns/op
Iteration   3: 171.719 ns/op
Iteration   4: 244.793 ns/op
Iteration   5: 226.568 ns/op
Iteration   6: 176.556 ns/op
Iteration   7: 182.781 ns/op
Iteration   8: 174.066 ns/op
Iteration   9: 173.124 ns/op
Iteration  10: 173.421 ns/op
Iteration  11: 214.129 ns/op
Iteration  12: 200.496 ns/op
Iteration  13: 168.912 ns/op
Iteration  14: 167.923 ns/op
Iteration  15: 182.786 ns/op
Iteration  16: 195.358 ns/op
Iteration  17: 165.590 ns/op
Iteration  18: 155.225 ns/op
Iteration  19: 172.992 ns/op
Iteration  20: 208.753 ns/op

Result: 185.313 ±(99.9%) 19.810 ns/op [Average]
  Statistics: (min, avg, max) = (155.225, 185.313, 244.793), stdev = 22.814
  Confidence interval (99.9%): [165.503, 205.123]


# Run progress: 66.67% complete, ETA 00:00:09
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.zexecutorService
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7537 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 961.239 ns/op
# Warmup Iteration   2: 2327.623 ns/op
# Warmup Iteration   3: 548.546 ns/op
# Warmup Iteration   4: 656.981 ns/op
# Warmup Iteration   5: 701.828 ns/op
# Warmup Iteration   6: 682.266 ns/op
# Warmup Iteration   7: 517.801 ns/op
# Warmup Iteration   8: 540.400 ns/op
# Warmup Iteration   9: 545.185 ns/op
# Warmup Iteration  10: 504.762 ns/op
# Warmup Iteration  11: 484.319 ns/op
# Warmup Iteration  12: 632.016 ns/op
# Warmup Iteration  13: 629.060 ns/op
# Warmup Iteration  14: 586.241 ns/op
# Warmup Iteration  15: 666.582 ns/op
# Warmup Iteration  16: 808.733 ns/op
# Warmup Iteration  17: 568.074 ns/op
# Warmup Iteration  18: 515.927 ns/op
# Warmup Iteration  19: 511.448 ns/op
# Warmup Iteration  20: 552.826 ns/op
Iteration   1: 639.807 ns/op
Iteration   2: 617.127 ns/op
Iteration   3: 472.135 ns/op
Iteration   4: 502.592 ns/op
Iteration   5: 613.285 ns/op
Iteration   6: 451.481 ns/op
Iteration   7: 463.179 ns/op
Iteration   8: 540.718 ns/op
Iteration   9: 504.321 ns/op
Iteration  10: 605.126 ns/op
Iteration  11: 480.621 ns/op
Iteration  12: 499.600 ns/op
Iteration  13: 476.148 ns/op
Iteration  14: 649.827 ns/op
Iteration  15: 458.245 ns/op
Iteration  16: 566.907 ns/op
Iteration  17: 480.438 ns/op
Iteration  18: 564.289 ns/op
Iteration  19: 484.646 ns/op
Iteration  20: 496.674 ns/op
 */

@State(Scope.Benchmark) public class TaskExecTest {
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
        Factories.init(ThreadsHandler.create());
    }
    private static final ConcurrentTaskExecutor<String> TASK_EXECUTOR = ConcurrentTaskExecutor.create(16);
    private static final TaskExecutor EXECUTOR = TASK_EXECUTOR.scaledThread();
    private static final ExecutorService JAVA = Executors.newFixedThreadPool(16);
    private static final Runnable RUNNABLE = new Runnable() {
        int anInt = 0;

        @Override
        public void run() {
            anInt++;
        }
    };

    public static void main(String[] args) {
        // Latency tests
        System.out.println("========= Starting tests: TRIDENT =========");

        System.out.println();

        System.out.println("========= Warming up the system =========");
        final BigDecimal[] decimal = { new BigDecimal(0) };
        for (int i = 0; i < 1_000; i++) {
            final long begin = System.nanoTime();
            TASK_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    long stop = System.nanoTime();
                    decimal[0] = decimal[0].add(new BigDecimal(stop - begin));
                }
            });

            if (i % 100 == 0 && i != 0) {
                System.out.println("Warmup iteration " + i + ": " + decimal[0].divide(
                                                   new BigDecimal(1_000), 3, RoundingMode.UNNECESSARY)
                                                   .toString() + " ns/op");
            }
        }

        System.out.println("========= Warm up complete =========");

        System.out.println();

        System.out.println("========= Starting tests =========");
        final BigDecimal[] big = { new BigDecimal(0) };
        for (int i = 0; i < 100_000_000; i++) {
            final long begin = System.nanoTime();
            TASK_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    long stop = System.nanoTime();
                    big[0] = big[0].add(new BigDecimal(stop - begin));
                }
            });

            if (i % 100_000 == 0)
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            if (i % 10_000_000 == 0 && i != 0) {
                System.out.println("Iteration " + i + ": " + big[0].divide(
                        new BigDecimal(100_000_000), 3, RoundingMode.UP)
                        .toString() + " ns/op");
            }
        }

        System.out.println("========= Ended test =========");

        System.out.println("Complete. " + big[0].divide(new BigDecimal(100_000_000), 3, RoundingMode.UP)
                .toString() + " ns/op");

        doJavaTest();
    }

    static void doJavaTest() {
        System.out.println("========= Starting tests: JAVA =========");

        System.out.println();

        System.out.println("========= Warming up the system =========");
        final BigDecimal[] decimal = { new BigDecimal(0) };
        for (int i = 0; i < 1_000; i++) {
            final long begin = System.nanoTime();
            JAVA.execute(new Runnable() {
                @Override
                public void run() {
                    long stop = System.nanoTime();
                    decimal[0] = decimal[0].add(new BigDecimal(stop - begin));
                }
            });

            if (i % 100 == 0 && i != 0) {
                System.out.println("Warmup iteration " + i + ": " + decimal[0].divide(
                        new BigDecimal(1_000), 3, RoundingMode.UNNECESSARY)
                        .toString() + " ns/op");
            }
        }

        System.out.println("========= Warm up complete =========");

        System.out.println();

        System.out.println("========= Starting tests =========");
        final BigDecimal[] big = { new BigDecimal(0) };
        for (int i = 0; i < 100_000_000; i++) {
            final long begin = System.nanoTime();
            JAVA.execute(new Runnable() {
                @Override
                public void run() {
                    long stop = System.nanoTime();
                    big[0] = big[0].add(new BigDecimal(stop - begin));
                }
            });

            if (i % 100_000 == 0)
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            if (i % 10_000_000 == 0 && i != 0) {
                System.out.println("Iteration " + i + ": " + big[0].divide(
                        new BigDecimal(100_000_000), 3, RoundingMode.UP)
                        .toString() + " ns/op");
            }
        }

        System.out.println("========= Ended test =========");

        System.out.println("Complete. " + big[0].divide(new BigDecimal(100_000_000), 3, RoundingMode.UP)
                .toString() + " ns/op");
    }

    //@Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
    private int cpuTokens;

    public static void main0(String... args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + TaskExecTest.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS).mode(Mode.AverageTime).warmupIterations(20)
                .warmupTime(TimeValue.milliseconds(10))              // ALLOWED TIME
                .measurementIterations(20)
                .measurementTime(TimeValue.milliseconds(10))         // ALLOWED TIME
                .forks(1)                                           // FORKS
                //.verbosity(VerboseMode.SILENT)                      // GRAPH
                .threads(4)                                         // THREADS
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        Benchmarks.chart(Benchmarks.parse(results), "ConcurrentTaskExecutor vs ExecutorService");
    }

    //@Benchmark
    public void control() {
        //Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void executorFactory() {
        //Blackhole.consumeCPU(cpuTokens);
        TASK_EXECUTOR.execute(RUNNABLE);
    }

    @Benchmark
    public void zexecutorService() {
        //Blackhole.consumeCPU(cpuTokens);
        JAVA.execute(RUNNABLE);
    }

    @Benchmark
    public void concurrentTaskExecutor() {
        //Blackhole.consumeCPU(cpuTokens);
        EXECUTOR.addTask(RUNNABLE);
    }
}
