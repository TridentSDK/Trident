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
import org.openjdk.jmh.runner.options.VerboseMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
========= Starting tests: TRIDENT =========

========= Warming up the system =========
Warmup iteration 100: 0.000 ns/op
Warmup iteration 200: 103717.305 ns/op
Warmup iteration 300: 294320.694 ns/op
Warmup iteration 400: 339208.573 ns/op
Warmup iteration 500: 339208.573 ns/op
Warmup iteration 600: 599198.814 ns/op
Warmup iteration 700: 599198.814 ns/op
Warmup iteration 800: 599198.814 ns/op
Warmup iteration 900: 599198.814 ns/op
========= Warm up complete =========

========= Starting tests =========
Iteration 10000000: 25528.165 ns/op
Iteration 20000000: 26968.149 ns/op
Iteration 30000000: 27278.767 ns/op
Iteration 40000000: 27512.523 ns/op
Iteration 50000000: 27720.204 ns/op
Iteration 60000000: 27957.150 ns/op
Iteration 70000000: 28344.386 ns/op
Iteration 80000000: 28609.102 ns/op
Iteration 90000000: 28894.367 ns/op
========= Ended test =========
Complete. 29172.143 ns/op

========= Starting tests: JAVA =========

========= Warming up the system =========
Warmup iteration 100: 19886.610 ns/op
Warmup iteration 200: 54039.265 ns/op
Warmup iteration 300: 98061.718 ns/op
Warmup iteration 400: 116969.406 ns/op
Warmup iteration 500: 143913.421 ns/op
Warmup iteration 600: 180630.039 ns/op
Warmup iteration 700: 203655.650 ns/op
Warmup iteration 800: 217234.967 ns/op
Warmup iteration 900: 222483.645 ns/op
========= Warm up complete =========

========= Starting tests =========
Iteration 10000000: 4628.598 ns/op
Iteration 20000000: 9274.451 ns/op
Iteration 30000000: 13210.221 ns/op
Iteration 40000000: 16514.152 ns/op
Iteration 50000000: 21006.449 ns/op
Iteration 60000000: 24361.641 ns/op
Iteration 70000000: 28390.795 ns/op
Iteration 80000000: 33168.349 ns/op
Iteration 90000000: 38249.874 ns/op
========= Ended test =========
Complete. 42901.430 ns/op

http://bit.ly/1xI4GcC
 */
@State(Scope.Benchmark)
public class TaskExecTest {
    private static ExecutorService JAVA = Executors.newCachedThreadPool();
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
                        RoundingMode.UP).toString() + " ns/op");
            }
        }

        System.out.println("========= Warm up complete =========");

        System.out.println();

        System.out.println("========= Starting tests =========");
        final BigDecimal[] big = { new BigDecimal(0) };
        int iterations = 100_000_000;
        CountDownLatch latch = new CountDownLatch(iterations);
        for (int i = 0; i < iterations; i++) {
            final long begin = System.nanoTime();
            TASK_EXECUTOR.execute(() -> {
                long stop = System.nanoTime();
                big[0] = big[0].add(new BigDecimal(stop - begin));
                latch.countDown();
            });

            if (i % 10_000_000 == 0 && i != 0) {
                System.out.println(
                        "Iteration " + i + ": " + big[0].divide(new BigDecimal(iterations), 3, RoundingMode.UP)
                                .toString() + " ns/op");
            }
        }
        latch.await();

        System.out.println("========= Ended test =========");

        System.out.println(
                "Complete. " + big[0].divide(new BigDecimal(iterations), 3, RoundingMode.UP).toString() + " ns/op");

        TASK_EXECUTOR.shutdownNow();
        // Clear all the useless tasks
        for (int i = 0; i < 10; i++) {
            System.gc();
        }

        // Need to pause for the threads to switch
        Thread.sleep(1000);

        doJavaTest();
    }

    static void doJavaTest() throws InterruptedException {
        System.out.println();

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
                        RoundingMode.UP).toString() + " ns/op");
            }
        }

        System.out.println("========= Warm up complete =========");

        System.out.println();

        System.out.println("========= Starting tests =========");
        final BigDecimal[] big = { new BigDecimal(0) };
        int iterations = 100_000_000;
        CountDownLatch latch = new CountDownLatch(iterations);
        for (int i = 0; i < iterations; i++) {
            final long begin = System.nanoTime();
            JAVA.execute(() -> {
                long stop = System.nanoTime();
                big[0] = big[0].add(new BigDecimal(stop - begin));
                latch.countDown();
            });

            if (i % 10_000_000 == 0 && i != 0) {
                System.out.println(
                        "Iteration " + i + ": " + big[0].divide(new BigDecimal(iterations), 3, RoundingMode.UP)
                                .toString() + " ns/op");
            }
        }
        latch.await();

        System.out.println("========= Ended test =========");

        System.out.println(
                "Complete. " + big[0].divide(new BigDecimal(iterations), 3, RoundingMode.UP).toString() + " ns/op");
        JAVA.shutdownNow();
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
                .verbosity(VerboseMode.SILENT)                      // GRAPH
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
