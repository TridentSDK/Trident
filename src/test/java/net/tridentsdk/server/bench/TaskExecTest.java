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
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import net.tridentsdk.util.TridentLogger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
# Run progress: 0.00% complete, ETA 00:00:01
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.concurrentTaskExecutor
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/home/agenttroll/idea-IU-139.659.2/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 219.315 ns/op
# Warmup Iteration   2: 285.642 ns/op
# Warmup Iteration   3: 155.855 ns/op
# Warmup Iteration   4: 204.775 ns/op
# Warmup Iteration   5: 141.379 ns/op
# Warmup Iteration   6: 161.633 ns/op
# Warmup Iteration   7: 137.856 ns/op
# Warmup Iteration   8: 188.196 ns/op
# Warmup Iteration   9: 170.865 ns/op
# Warmup Iteration  10: 148.636 ns/op
# Warmup Iteration  11: 140.670 ns/op
# Warmup Iteration  12: 174.672 ns/op
# Warmup Iteration  13: 245.167 ns/op
# Warmup Iteration  14: 206.736 ns/op
# Warmup Iteration  15: 165.800 ns/op
# Warmup Iteration  16: 137.532 ns/op
# Warmup Iteration  17: 146.333 ns/op
# Warmup Iteration  18: 175.270 ns/op
# Warmup Iteration  19: 145.019 ns/op
# Warmup Iteration  20: 165.366 ns/op
Iteration   1: 139.423 ns/op
Iteration   2: 177.423 ns/op
Iteration   3: 190.496 ns/op
Iteration   4: 149.753 ns/op
Iteration   5: 134.228 ns/op
Iteration   6: 145.109 ns/op
Iteration   7: 249.146 ns/op
Iteration   8: 144.618 ns/op
Iteration   9: 138.846 ns/op
Iteration  10: 157.834 ns/op
Iteration  11: 136.593 ns/op
Iteration  12: 177.654 ns/op
Iteration  13: 148.664 ns/op
Iteration  14: 142.966 ns/op
Iteration  15: 155.117 ns/op
Iteration  16: 138.167 ns/op
Iteration  17: 159.238 ns/op
Iteration  18: 145.195 ns/op
Iteration  19: 141.196 ns/op
Iteration  20: 151.857 ns/op

Result: 156.176 ±(99.9%) 23.069 ns/op [Average]
  Statistics: (min, avg, max) = (134.228, 156.176, 249.146), stdev = 26.566
  Confidence interval (99.9%): [133.107, 179.245]


# Run progress: 33.33% complete, ETA 00:00:18
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.executorFactory
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/home/agenttroll/idea-IU-139.659.2/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 2163.737 ns/op
# Warmup Iteration   2: 715.500 ns/op
# Warmup Iteration   3: 577.522 ns/op
# Warmup Iteration   4: 541.910 ns/op
# Warmup Iteration   5: 685.531 ns/op
# Warmup Iteration   6: 618.261 ns/op
# Warmup Iteration   7: 598.105 ns/op
# Warmup Iteration   8: 644.750 ns/op
# Warmup Iteration   9: 669.798 ns/op
# Warmup Iteration  10: 652.997 ns/op
# Warmup Iteration  11: 644.813 ns/op
# Warmup Iteration  12: 582.286 ns/op
# Warmup Iteration  13: 566.301 ns/op
# Warmup Iteration  14: 599.330 ns/op
# Warmup Iteration  15: 673.478 ns/op
# Warmup Iteration  16: 606.519 ns/op
# Warmup Iteration  17: 695.408 ns/op
# Warmup Iteration  18: 656.450 ns/op
# Warmup Iteration  19: 569.002 ns/op
# Warmup Iteration  20: 671.927 ns/op
Iteration   1: 637.646 ns/op
Iteration   2: 626.256 ns/op
Iteration   3: 582.457 ns/op
Iteration   4: 657.705 ns/op
Iteration   5: 636.927 ns/op
Iteration   6: 652.630 ns/op
Iteration   7: 558.914 ns/op
Iteration   8: 664.720 ns/op
Iteration   9: 595.476 ns/op
Iteration  10: 555.478 ns/op
Iteration  11: 571.490 ns/op
Iteration  12: 629.394 ns/op
Iteration  13: 552.070 ns/op
Iteration  14: 585.507 ns/op
Iteration  15: 686.352 ns/op
Iteration  16: 527.302 ns/op
Iteration  17: 665.020 ns/op
Iteration  18: 637.844 ns/op
Iteration  19: 612.158 ns/op
Iteration  20: 626.036 ns/op

Result: 613.069 ±(99.9%) 38.707 ns/op [Average]
  Statistics: (min, avg, max) = (527.302, 613.069, 686.352), stdev = 44.575
  Confidence interval (99.9%): [574.362, 651.776]


# Run progress: 66.67% complete, ETA 00:00:09
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.zexecutorService
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/home/agenttroll/idea-IU-139.659.2/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 418.382 ns/op
# Warmup Iteration   2: 316.825 ns/op
# Warmup Iteration   3: 446.281 ns/op
# Warmup Iteration   4: 600.024 ns/op
# Warmup Iteration   5: 411.992 ns/op
# Warmup Iteration   6: 429.422 ns/op
# Warmup Iteration   7: 455.621 ns/op
# Warmup Iteration   8: 416.499 ns/op
# Warmup Iteration   9: 926.426 ns/op
# Warmup Iteration  10: 452.824 ns/op
# Warmup Iteration  11: 483.843 ns/op
# Warmup Iteration  12: 466.315 ns/op
# Warmup Iteration  13: 394.396 ns/op
# Warmup Iteration  14: 400.922 ns/op
# Warmup Iteration  15: 510.021 ns/op
# Warmup Iteration  16: 501.432 ns/op
# Warmup Iteration  17: 485.537 ns/op
# Warmup Iteration  18: 437.357 ns/op
# Warmup Iteration  19: 483.114 ns/op
# Warmup Iteration  20: 562.341 ns/op
Iteration   1: 358.942 ns/op
Iteration   2: 516.338 ns/op
Iteration   3: 748.485 ns/op
Iteration   4: 393.251 ns/op
Iteration   5: 364.233 ns/op
Iteration   6: 415.009 ns/op
Iteration   7: 468.357 ns/op
Iteration   8: 338.761 ns/op
Iteration   9: 463.513 ns/op
Iteration  10: 415.037 ns/op
Iteration  11: 321.793 ns/op
Iteration  12: 399.287 ns/op
Iteration  13: 465.627 ns/op
Iteration  14: 440.006 ns/op
Iteration  15: 448.482 ns/op
Iteration  16: 609.140 ns/op
Iteration  17: 499.988 ns/op
Iteration  18: 491.382 ns/op
Iteration  19: 451.590 ns/op
Iteration  20: 384.370 ns/op
 */

@State(Scope.Benchmark)
public class TaskExecTest {
    static {
        TridentLogger.init();
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
    }

    private static final ExecutorService JAVA = Executors.newFixedThreadPool(13);
    private static final Runnable RUNNABLE = new Runnable() {
        int anInt = 0;

        @Override
        public void run() {
            anInt++;
        }
    };
    private static ConcurrentTaskExecutor<String> TASK_EXECUTOR;
    private static TaskExecutor EXECUTOR;
    //@Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
    private int cpuTokens;

    public static void main2(String[] args) {
        new TaskExecTest().setup();

        while (true) {
            TASK_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }

    public static void main0(String[] args) throws InterruptedException {
        new TaskExecTest().setup();

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
                System.out.println("Warmup iteration " + i + ": " + decimal[0].divide(new BigDecimal(1_000), 3,
                        RoundingMode.UNNECESSARY).toString() + " ns/op");
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
                System.out.println(
                        "Iteration " + i + ": " + big[0].divide(new BigDecimal(100_000_000), 3, RoundingMode.UP)
                                .toString() + " ns/op");
            }
        }

        System.out.println("========= Ended test =========");

        System.out.println(
                "Complete. " + big[0].divide(new BigDecimal(100_000_000), 3, RoundingMode.UP).toString() + " ns/op");

        // Clear all the useless tasks
        TASK_EXECUTOR.shutdown();
        for (int i = 0; i < 10; i++) {
            System.gc();
        }

        // Need to pause for the threads to switch
        Thread.sleep(100000);

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
                System.out.println("Warmup iteration " + i + ": " + decimal[0].divide(new BigDecimal(1_000), 3,
                        RoundingMode.UNNECESSARY).toString() + " ns/op");
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
                System.out.println(
                        "Iteration " + i + ": " + big[0].divide(new BigDecimal(100_000_000), 3, RoundingMode.UP)
                                .toString() + " ns/op");
            }
        }

        System.out.println("========= Ended test =========");

        System.out.println(
                "Complete. " + big[0].divide(new BigDecimal(100_000_000), 3, RoundingMode.UP).toString() + " ns/op");
    }

    public static void main1(String[] args) {
        new TaskExecTest().setup();

        for (int i = 0; i < 20000; i++) {
            final int finalI = i;
            JAVA.execute(new Runnable() {
                @Override
                public void run() {
                    TASK_EXECUTOR.execute(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(finalI);
                        }
                    });
                }
            });
        }
    }

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + TaskExecTest.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS).mode(Mode.AverageTime).warmupIterations(20).warmupTime(
                        TimeValue.milliseconds(10))              // ALLOWED TIME
                .measurementIterations(20).measurementTime(TimeValue.milliseconds(10))         // ALLOWED TIME
                .forks(1)                                           // FORKS
                        //.verbosity(VerboseMode.SILENT)                      // GRAPH
                .threads(4)                                         // THREADS
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        Benchmarks.chart(Benchmarks.parse(results), "ConcurrentTaskExecutor vs ExecutorService");
    }

    @Setup
    public void setup() {
        TASK_EXECUTOR = ConcurrentTaskExecutor.create(13, "TaskExecTest");
        EXECUTOR = TASK_EXECUTOR.scaledThread();
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
