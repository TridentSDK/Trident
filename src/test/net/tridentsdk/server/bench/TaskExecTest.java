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
import org.openjdk.jmh.annotations.*;
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
import java.util.concurrent.*;

/*
# Run progress: 0.00% complete, ETA 00:00:01
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.concurrentTaskExecutor
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7547 -Didea.launcher.bin.path=/home/agenttroll/idea-IU-139.659.2/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 185.148 ns/op
# Warmup Iteration   2: 4069331.189 ns/op
# Warmup Iteration   3: 242.858 ns/op
# Warmup Iteration   4: 159.020 ns/op
# Warmup Iteration   5: 161.967 ns/op
# Warmup Iteration   6: 142.806 ns/op
# Warmup Iteration   7: 173.058 ns/op
# Warmup Iteration   8: 201.502 ns/op
# Warmup Iteration   9: 159.542 ns/op
# Warmup Iteration  10: 180.848 ns/op
# Warmup Iteration  11: 184.059 ns/op
# Warmup Iteration  12: 166.272 ns/op
# Warmup Iteration  13: 144.897 ns/op
# Warmup Iteration  14: 150.372 ns/op
# Warmup Iteration  15: 175.727 ns/op
# Warmup Iteration  16: 190.755 ns/op
# Warmup Iteration  17: 147.131 ns/op
# Warmup Iteration  18: 147.997 ns/op
# Warmup Iteration  19: 148.328 ns/op
# Warmup Iteration  20: 216.091 ns/op
Iteration   1: 167.419 ns/op
Iteration   2: 155.956 ns/op
Iteration   3: 139.882 ns/op
Iteration   4: 163.833 ns/op
Iteration   5: 171.991 ns/op
Iteration   6: 156.741 ns/op
Iteration   7: 144.120 ns/op
Iteration   8: 155.814 ns/op
Iteration   9: 167.400 ns/op
Iteration  10: 146.550 ns/op
Iteration  11: 174.257 ns/op
Iteration  12: 145.339 ns/op
Iteration  13: 186.664 ns/op
Iteration  14: 157.914 ns/op
Iteration  15: 138.310 ns/op
Iteration  16: 208.806 ns/op
Iteration  17: 171.702 ns/op
Iteration  18: 388.687 ns/op
Iteration  19: 205.263 ns/op
Iteration  20: 162.675 ns/op

Result: 175.466 ±(99.9%) 46.653 ns/op [Average]
  Statistics: (min, avg, max) = (138.310, 175.466, 388.687), stdev = 53.726
  Confidence interval (99.9%): [128.813, 222.119]


# Run progress: 33.33% complete, ETA 00:00:18
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.executorFactory
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7547 -Didea.launcher.bin.path=/home/agenttroll/idea-IU-139.659.2/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 6120.575 ns/op
# Warmup Iteration   2: 581.558 ns/op
# Warmup Iteration   3: 474.803 ns/op
# Warmup Iteration   4: 596.237 ns/op
# Warmup Iteration   5: 538.567 ns/op
# Warmup Iteration   6: 502.149 ns/op
# Warmup Iteration   7: 505.541 ns/op
# Warmup Iteration   8: 489.634 ns/op
# Warmup Iteration   9: 523.221 ns/op
# Warmup Iteration  10: 519.844 ns/op
# Warmup Iteration  11: 508.277 ns/op
# Warmup Iteration  12: 491.924 ns/op
# Warmup Iteration  13: 541.791 ns/op
# Warmup Iteration  14: 496.153 ns/op
# Warmup Iteration  15: 514.919 ns/op
# Warmup Iteration  16: 517.730 ns/op
# Warmup Iteration  17: 555.552 ns/op
# Warmup Iteration  18: 505.587 ns/op
# Warmup Iteration  19: 509.407 ns/op
# Warmup Iteration  20: 492.392 ns/op
Iteration   1: 497.816 ns/op
Iteration   2: 511.653 ns/op
Iteration   3: 507.683 ns/op
Iteration   4: 696.446 ns/op
Iteration   5: 533.837 ns/op
Iteration   6: 490.147 ns/op
Iteration   7: 520.532 ns/op
Iteration   8: 1608.380 ns/op
Iteration   9: 556.288 ns/op
Iteration  10: 591.093 ns/op
Iteration  11: 496.549 ns/op
Iteration  12: 497.442 ns/op
Iteration  13: 486.791 ns/op
Iteration  14: 497.051 ns/op
Iteration  15: 487.380 ns/op
Iteration  16: 534.568 ns/op
Iteration  17: 504.520 ns/op
Iteration  18: 552.875 ns/op
Iteration  19: 475.768 ns/op
Iteration  20: 557.079 ns/op

Result: 580.195 ±(99.9%) 214.562 ns/op [Average]
  Statistics: (min, avg, max) = (475.768, 580.195, 1608.380), stdev = 247.090
  Confidence interval (99.9%): [365.633, 794.757]


# Run progress: 66.67% complete, ETA 00:00:09
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.zexecutorService
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7547 -Didea.launcher.bin.path=/home/agenttroll/idea-IU-139.659.2/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 10413.885 ns/op
# Warmup Iteration   2: 320.260 ns/op
# Warmup Iteration   3: 702.735 ns/op
# Warmup Iteration   4: 1227.080 ns/op
# Warmup Iteration   5: 501.978 ns/op
# Warmup Iteration   6: 424.570 ns/op
# Warmup Iteration   7: 473.661 ns/op
# Warmup Iteration   8: 457.455 ns/op
# Warmup Iteration   9: 462.080 ns/op
# Warmup Iteration  10: 553.377 ns/op
# Warmup Iteration  11: 467.909 ns/op
# Warmup Iteration  12: 428.878 ns/op
# Warmup Iteration  13: 441.982 ns/op
# Warmup Iteration  14: 473.931 ns/op
# Warmup Iteration  15: 557.871 ns/op
# Warmup Iteration  16: 500.453 ns/op
# Warmup Iteration  17: 572.301 ns/op
# Warmup Iteration  18: 565.199 ns/op
# Warmup Iteration  19: 543.752 ns/op
# Warmup Iteration  20: 583.444 ns/op
Iteration   1: 537.213 ns/op
Iteration   2: 431.084 ns/op
Iteration   3: 463.535 ns/op
Iteration   4: 515.218 ns/op
Iteration   5: 558.815 ns/op
Iteration   6: 437.751 ns/op
Iteration   7: 475.910 ns/op
Iteration   8: 496.112 ns/op
Iteration   9: 877.763 ns/op
Iteration  10: 538.377 ns/op
Iteration  11: 561.707 ns/op
Iteration  12: 482.599 ns/op
Iteration  13: 494.747 ns/op
Iteration  14: 601.233 ns/op
Iteration  15: 519.853 ns/op
Iteration  16: 648.214 ns/op
Iteration  17: 552.371 ns/op
Iteration  18: 516.099 ns/op
Iteration  19: 442.282 ns/op
Iteration  20: 700.935 ns/op
 */

@State(Scope.Benchmark) public class TaskExecTest {
    static {
        TridentLogger.init();
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
    }
    private static ConcurrentTaskExecutor<String> TASK_EXECUTOR;
    private static TaskExecutor EXECUTOR;
    private static final ExecutorService JAVA = Executors.newFixedThreadPool(13);
    private static final Runnable RUNNABLE = new Runnable() {
        int anInt = 0;

        @Override
        public void run() {
            anInt++;
        }
    };

    @Setup
    public void setup() {
        TASK_EXECUTOR = ConcurrentTaskExecutor.create(13);
        EXECUTOR = TASK_EXECUTOR.scaledThread();
    }

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

    public static void main(String[] args) throws InterruptedException {
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
