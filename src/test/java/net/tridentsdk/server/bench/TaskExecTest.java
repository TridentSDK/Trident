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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
========= Starting tests: TRIDENT =========

========= Warming up the system =========
Warmup iteration 100: 104880.986 ns/op
Warmup iteration 200: 110094.981 ns/op
Warmup iteration 300: 107070.060 ns/op
Warmup iteration 400: 117896.509 ns/op
Warmup iteration 500: 160579.276 ns/op
Warmup iteration 600: 242080.127 ns/op
Warmup iteration 700: 310637.597 ns/op
Warmup iteration 800: 355278.822 ns/op
Warmup iteration 900: 379616.754 ns/op
========= Warm up complete =========

========= Starting tests =========
Iteration 10000000: 231.339 ns/op
Iteration 20000000: 455.253 ns/op
Iteration 30000000: 675.527 ns/op
Iteration 40000000: 894.878 ns/op
Iteration 50000000: 1121.887 ns/op
Iteration 60000000: 1341.498 ns/op
Iteration 70000000: 1560.445 ns/op
Iteration 80000000: 1784.188 ns/op
Iteration 90000000: 2009.559 ns/op
========= Ended test =========
Complete. 2236.833 ns/op
========= Starting tests: TRIDENT =========

========= Warming up the system =========
Warmup iteration 100: 24088.294 ns/op
Warmup iteration 200: 90527.924 ns/op
Warmup iteration 300: 157467.730 ns/op
Warmup iteration 400: 186139.146 ns/op
Warmup iteration 500: 223752.503 ns/op
Warmup iteration 600: 255329.002 ns/op
Warmup iteration 700: 261051.025 ns/op
Warmup iteration 800: 296470.781 ns/op
Warmup iteration 900: 327810.313 ns/op
========= Warm up complete =========

========= Starting tests =========
Iteration 10000000: 246.506 ns/op
Iteration 20000000: 493.351 ns/op
Iteration 30000000: 744.163 ns/op
Iteration 40000000: 997.812 ns/op
Iteration 50000000: 1249.793 ns/op
Iteration 60000000: 1503.038 ns/op
Iteration 70000000: 1752.930 ns/op
Iteration 80000000: 2004.659 ns/op
Iteration 90000000: 2270.577 ns/op
========= Ended test =========
Complete. 2523.961 ns/op

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

    public static void main0(String[] args) throws InterruptedException {
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
        for (int i = 0; i < iterations; i++) {
            CountDownLatch latch = new CountDownLatch(1);
            final long begin = System.nanoTime();
            TASK_EXECUTOR.execute(() -> {
                long stop = System.nanoTime();
                big[0] = big[0].add(new BigDecimal(stop - begin));
                latch.countDown();
            });

            latch.await();

            if (i % 10_000_000 == 0 && i != 0) {
                System.out.println(
                        "Iteration " + i + ": " + big[0].divide(new BigDecimal(iterations), 3, RoundingMode.UP)
                                .toString() + " ns/op");
            }
        }

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
        System.out.println("========= Starting tests: TRIDENT =========");

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
        for (int i = 0; i < iterations; i++) {
            CountDownLatch latch = new CountDownLatch(1);
            final long begin = System.nanoTime();
            JAVA.execute(() -> {
                long stop = System.nanoTime();
                big[0] = big[0].add(new BigDecimal(stop - begin));
                latch.countDown();
            });

            latch.await();

            if (i % 100_000 == 0)
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            if (i % 10_000_000 == 0 && i != 0) {
                System.out.println(
                        "Iteration " + i + ": " + big[0].divide(new BigDecimal(iterations), 3, RoundingMode.UP)
                                .toString() + " ns/op");
            }
        }

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

    public static void main(String... args) throws RunnerException {
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
