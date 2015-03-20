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


import net.tridentsdk.concurrent.TaskExecutor;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
========= Starting tests: TRIDENT =========

========= Warming up the system =========
Warmup iteration 100: 4889.212 ns/op
Warmup iteration 200: 33791.933 ns/op
Warmup iteration 300: 81591.043 ns/op
Warmup iteration 400: 85302.103 ns/op
Warmup iteration 500: 125701.439 ns/op
Warmup iteration 600: 141949.020 ns/op
Warmup iteration 700: 199897.850 ns/op
Warmup iteration 800: 267967.419 ns/op
Warmup iteration 900: 337249.555 ns/op
========= Warm up complete =========

========= Starting tests =========
Iteration 10000000: 138777.004 ns/op
Iteration 20000000: 224413.591 ns/op
Iteration 30000000: 283868.316 ns/op
Iteration 40000000: 341304.927 ns/op
Iteration 50000000: 408210.938 ns/op
Iteration 60000000: 477185.787 ns/op
Iteration 70000000: 559801.963 ns/op
Iteration 80000000: 642992.347 ns/op
Iteration 90000000: 725798.775 ns/op
========= Ended test =========
Complete. 801960.581 ns/op

========= Starting tests: JAVA =========

========= Warming up the system =========
Warmup iteration 100: 11450.120 ns/op
Warmup iteration 200: 87682.872 ns/op
Warmup iteration 300: 99953.454 ns/op
Warmup iteration 400: 99953.454 ns/op
Warmup iteration 500: 99953.454 ns/op
Warmup iteration 600: 1254196.607 ns/op
Warmup iteration 700: 1254196.607 ns/op
Warmup iteration 800: 1254196.607 ns/op
Warmup iteration 900: 1254196.607 ns/op
========= Warm up complete =========

========= Starting tests =========
Iteration 10000000: 486585.494 ns/op
Iteration 20000000: 1011151.414 ns/op
Iteration 30000000: 1515163.430 ns/op
Iteration 40000000: 2052993.060 ns/op
Iteration 50000000: 2572005.314 ns/op
Iteration 60000000: 3104995.588 ns/op
Iteration 70000000: 3668143.197 ns/op
Iteration 80000000: 4245930.511 ns/op
Iteration 90000000: 4726552.187 ns/op
========= Ended test =========
Complete. 5229887.591 ns/op

http://bit.ly/1Fwu7W6
 */
@State(Scope.Benchmark)
public class TaskExecTest {
    private static ExecutorService JAVA = Executors.newFixedThreadPool(4);
    private static final Runnable RUNNABLE = new Runnable() {
        int anInt = 0;

        @Override
        public void run() {
            anInt++;
        }
    };
    private static ConcurrentTaskExecutor TASK_EXECUTOR = ConcurrentTaskExecutor.create(4, "Test");
    private static TaskExecutor EXECUTOR = TASK_EXECUTOR.scaledThread();

    public static void main2(String[] args) {
        while (true) {
            TASK_EXECUTOR.execute(() -> {
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Latency tests
        System.out.println("========= Starting tests: TRIDENT =========");

        System.out.println();

        System.out.println("========= Warming up the system =========");
        final BigDecimal[] decimal = { new BigDecimal(0) };
        for (int i = 0; i < 1_000; i++) {
            final long begin = System.nanoTime();
            TASK_EXECUTOR.execute(() -> {
                long stop = System.nanoTime();
                decimal[0] = decimal[0].add(new BigDecimal(stop - begin));
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
            TASK_EXECUTOR.execute(() -> {
                long stop = System.nanoTime();
                big[0] = big[0].add(new BigDecimal(stop - begin));
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
            JAVA.execute(() -> {
                long stop = System.nanoTime();
                decimal[0] = decimal[0].add(new BigDecimal(stop - begin));
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
            JAVA.execute(() -> {
                long stop = System.nanoTime();
                big[0] = big[0].add(new BigDecimal(stop - begin));
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
        for (int i = 0; i < 20000; i++) {
            final int finalI = i;
            JAVA.execute(() -> TASK_EXECUTOR.execute(() -> System.out.println(finalI)));
        }
    }

    @Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
    private int cpuTokens;

    @TearDown
    public void tearDown() {
        JAVA.shutdownNow();
        TASK_EXECUTOR.shutdownNow();
    }

    public static void main0(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + TaskExecTest.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(20)
                .warmupTime(TimeValue.nanoseconds(5000))             // ALLOWED TIME
                .measurementIterations(5)
                .measurementTime(TimeValue.nanoseconds(5000))       // ALLOWED TIME
                .forks(1)                                           // FORKS
                //.verbosity(VerboseMode.SILENT)                      // GRAPH
                .threads(4)                                         // THREADS
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        Benchmarks.chart(Benchmarks.parse(results), "ConcurrentTaskExecutor vs ExecutorService");
    }

    @Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void tridentExecute() {
        Blackhole.consumeCPU(cpuTokens);
        TASK_EXECUTOR.execute(RUNNABLE);
    }

    @Benchmark
    public void javaExecute() {
        Blackhole.consumeCPU(cpuTokens);
        JAVA.execute(RUNNABLE);
    }

    @Benchmark
    public void tridentThreadExecute() {
        Blackhole.consumeCPU(cpuTokens);
        EXECUTOR.addTask(RUNNABLE);
    }
}
