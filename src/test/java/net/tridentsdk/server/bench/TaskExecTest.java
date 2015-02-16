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
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.distributedExecute
# VM invoker: C:\Program Files\Java\jdk1.8.0_31\jre\bin\java.exe
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=C:\Program Files (x86)\JetBrains\IntelliJ IDEA 14.0.3\bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 15323.824 ns/op
# Warmup Iteration   2: 514.903 ns/op
# Warmup Iteration   3: 784.067 ns/op
# Warmup Iteration   4: 498.686 ns/op
# Warmup Iteration   5: 491.977 ns/op
# Warmup Iteration   6: 538.584 ns/op
# Warmup Iteration   7: 556.378 ns/op
# Warmup Iteration   8: 525.556 ns/op
# Warmup Iteration   9: 588.892 ns/op
# Warmup Iteration  10: 478.544 ns/op
# Warmup Iteration  11: 475.141 ns/op
# Warmup Iteration  12: 460.852 ns/op
# Warmup Iteration  13: 527.672 ns/op
# Warmup Iteration  14: 454.100 ns/op
# Warmup Iteration  15: 572.393 ns/op
# Warmup Iteration  16: 463.960 ns/op
# Warmup Iteration  17: 501.673 ns/op
# Warmup Iteration  18: 533.276 ns/op
# Warmup Iteration  19: 469.473 ns/op
# Warmup Iteration  20: 634.866 ns/op
Iteration   1: 497.313 ns/op
Iteration   2: 697.293 ns/op
Iteration   3: 501.893 ns/op
Iteration   4: 464.992 ns/op
Iteration   5: 509.492 ns/op
Iteration   6: 452.493 ns/op
Iteration   7: 524.616 ns/op
Iteration   8: 520.940 ns/op
Iteration   9: 570.278 ns/op
Iteration  10: 559.092 ns/op
Iteration  11: 550.439 ns/op
Iteration  12: 475.379 ns/op
Iteration  13: 632.006 ns/op
Iteration  14: 535.117 ns/op
Iteration  15: 605.714 ns/op
Iteration  16: 542.674 ns/op
Iteration  17: 514.061 ns/op
Iteration  18: 509.916 ns/op
Iteration  19: 477.566 ns/op
Iteration  20: 600.391 ns/op

Result: 537.083 ±(99.9%) 52.769 ns/op [Average]
  Statistics: (min, avg, max) = (452.493, 537.083, 697.293), stdev = 60.768
  Confidence interval (99.9%): [484.315, 589.852]


# Run progress: 33.33% complete, ETA 00:00:19
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.singleExecute
# VM invoker: C:\Program Files\Java\jdk1.8.0_31\jre\bin\java.exe
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=C:\Program Files (x86)\JetBrains\IntelliJ IDEA 14.0.3\bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 295.270 ns/op
# Warmup Iteration   2: 441.193 ns/op
# Warmup Iteration   3: 223.689 ns/op
# Warmup Iteration   4: 301.200 ns/op
# Warmup Iteration   5: 228.381 ns/op
# Warmup Iteration   6: 255.743 ns/op
# Warmup Iteration   7: 282.422 ns/op
# Warmup Iteration   8: 218.367 ns/op
# Warmup Iteration   9: 183.986 ns/op
# Warmup Iteration  10: 271.572 ns/op
# Warmup Iteration  11: 235.442 ns/op
# Warmup Iteration  12: 237.506 ns/op
# Warmup Iteration  13: 259.248 ns/op
# Warmup Iteration  14: 285.803 ns/op
# Warmup Iteration  15: 252.367 ns/op
# Warmup Iteration  16: 343.397 ns/op
# Warmup Iteration  17: 235.349 ns/op
# Warmup Iteration  18: 199.153 ns/op
# Warmup Iteration  19: 220.378 ns/op
# Warmup Iteration  20: 219.086 ns/op
Iteration   1: 236.842 ns/op
Iteration   2: 255.921 ns/op
Iteration   3: 236.880 ns/op
Iteration   4: 234.840 ns/op
Iteration   5: 237.947 ns/op
Iteration   6: 231.316 ns/op
Iteration   7: 237.682 ns/op
Iteration   8: 243.027 ns/op
Iteration   9: 269.142 ns/op
Iteration  10: 256.697 ns/op
Iteration  11: 275.731 ns/op
Iteration  12: 242.439 ns/op
Iteration  13: 272.348 ns/op
Iteration  14: 312.567 ns/op
Iteration  15: 247.488 ns/op
Iteration  16: 203.017 ns/op
Iteration  17: 209.046 ns/op
Iteration  18: 235.217 ns/op
Iteration  19: 241.222 ns/op
Iteration  20: 215.487 ns/op

Result: 244.743 ±(99.9%) 21.525 ns/op [Average]
  Statistics: (min, avg, max) = (203.017, 244.743, 312.567), stdev = 24.788
  Confidence interval (99.9%): [223.218, 266.268]


# Run progress: 66.67% complete, ETA 00:00:09
# Warmup: 20 iterations, 10 ms each
# Measurement: 20 iterations, 10 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.zexecutorService
# VM invoker: C:\Program Files\Java\jdk1.8.0_31\jre\bin\java.exe
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=C:\Program Files (x86)\JetBrains\IntelliJ IDEA 14.0.3\bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 683.244 ns/op
# Warmup Iteration   2: 706.493 ns/op
# Warmup Iteration   3: 585.342 ns/op
# Warmup Iteration   4: 637.641 ns/op
# Warmup Iteration   5: 546.499 ns/op
# Warmup Iteration   6: 638.842 ns/op
# Warmup Iteration   7: 805.836 ns/op
# Warmup Iteration   8: 633.173 ns/op
# Warmup Iteration   9: 602.662 ns/op
# Warmup Iteration  10: 584.072 ns/op
# Warmup Iteration  11: 663.739 ns/op
# Warmup Iteration  12: 591.832 ns/op
# Warmup Iteration  13: 645.415 ns/op
# Warmup Iteration  14: 628.158 ns/op
# Warmup Iteration  15: 657.441 ns/op
# Warmup Iteration  16: 706.322 ns/op
# Warmup Iteration  17: 685.334 ns/op
# Warmup Iteration  18: 640.320 ns/op
# Warmup Iteration  19: 548.210 ns/op
# Warmup Iteration  20: 659.552 ns/op
Iteration   1: 462.483 ns/op
Iteration   2: 503.798 ns/op
Iteration   3: 597.408 ns/op
Iteration   4: 570.590 ns/op
Iteration   5: 653.233 ns/op
Iteration   6: 879.512 ns/op
Iteration   7: 667.518 ns/op
Iteration   8: 694.147 ns/op
Iteration   9: 598.996 ns/op
Iteration  10: 481.070 ns/op
Iteration  11: 535.380 ns/op
Iteration  12: 505.087 ns/op
Iteration  13: 645.935 ns/op
Iteration  14: 641.042 ns/op
Iteration  15: 623.350 ns/op
Iteration  16: 597.833 ns/op
Iteration  17: 736.159 ns/op
Iteration  18: 532.420 ns/op
Iteration  19: 634.377 ns/op
Iteration  20: 634.798 ns/op

Result: 609.757 ±(99.9%) 84.211 ns/op [Average]
  Statistics: (min, avg, max) = (462.483, 609.757, 879.512), stdev = 96.978
  Confidence interval (99.9%): [525.546, 693.968]


# Run complete. Total time: 00:00:27

Benchmark                                   Mode   Samples        Score  Score error    Units
n.t.s.b.TaskExecTest.distributedExecute     avgt        20      537.083       52.769    ns/op
n.t.s.b.TaskExecTest.singleExecute          avgt        20      244.743       21.525    ns/op
n.t.s.b.TaskExecTest.zexecutorService       avgt        20      609.757       84.211    ns/op
 */

@State(Scope.Benchmark)
public class TaskExecTest {
    static {
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
    }

    private static ExecutorService JAVA = Executors.newFixedThreadPool(13);
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
        while (true) {
            TASK_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }

    public static void main0(String[] args) throws InterruptedException {
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
        Thread.sleep(1000);

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

    //@Benchmark
    public void control() {
        //Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void distributedExecute() {
        //Blackhole.consumeCPU(cpuTokens);
        TASK_EXECUTOR.execute(RUNNABLE);
    }

    @Benchmark
    public void zexecutorService() {
        //Blackhole.consumeCPU(cpuTokens);
        JAVA.execute(RUNNABLE);
    }

    @Setup
    public void setup0() {
        TASK_EXECUTOR = ConcurrentTaskExecutor.create(13, "TaskExecTest");
        EXECUTOR = TASK_EXECUTOR.scaledThread();
    }

    @Setup(Level.Iteration)
    public void setup() {
        JAVA = Executors.newFixedThreadPool(13);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        JAVA.shutdownNow();
    }

    @Benchmark
    public void singleExecute() {
        //Blackhole.consumeCPU(cpuTokens);
        EXECUTOR.addTask(RUNNABLE);
    }
}
