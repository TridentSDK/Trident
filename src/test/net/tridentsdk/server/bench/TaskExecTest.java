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

import java.util.Collection;
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
# Run progress: 0.00% complete, ETA 00:01:40
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.equiv
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7542 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile
.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 620.698 ns/op
# Warmup Iteration   2: 577.828 ns/op
# Warmup Iteration   3: 588.218 ns/op
# Warmup Iteration   4: 572.028 ns/op
# Warmup Iteration   5: 562.270 ns/op
# Warmup Iteration   6: 573.854 ns/op
# Warmup Iteration   7: 566.267 ns/op
# Warmup Iteration   8: 569.248 ns/op
# Warmup Iteration   9: 564.638 ns/op
# Warmup Iteration  10: 564.623 ns/op
# Warmup Iteration  11: 561.547 ns/op
# Warmup Iteration  12: 554.247 ns/op
# Warmup Iteration  13: 554.735 ns/op
# Warmup Iteration  14: 561.941 ns/op
# Warmup Iteration  15: 569.643 ns/op
# Warmup Iteration  16: 562.636 ns/op
# Warmup Iteration  17: 539.888 ns/op
# Warmup Iteration  18: 562.732 ns/op
# Warmup Iteration  19: 567.646 ns/op
# Warmup Iteration  20: 545.153 ns/op
# Warmup Iteration  21: 540.580 ns/op
# Warmup Iteration  22: 554.776 ns/op
# Warmup Iteration  23: 564.866 ns/op
# Warmup Iteration  24: 550.070 ns/op
# Warmup Iteration  25: 594.103 ns/op
Iteration   1: 555.633 ns/op
Iteration   2: 557.401 ns/op
Iteration   3: 584.541 ns/op
Iteration   4: 563.625 ns/op
Iteration   5: 560.032 ns/op
Iteration   6: 560.501 ns/op
Iteration   7: 567.653 ns/op
Iteration   8: 566.979 ns/op
Iteration   9: 555.060 ns/op
Iteration  10: 555.313 ns/op
Iteration  11: 560.158 ns/op
Iteration  12: 566.413 ns/op
Iteration  13: 541.716 ns/op
Iteration  14: 566.410 ns/op
Iteration  15: 555.814 ns/op
Iteration  16: 554.555 ns/op
Iteration  17: 563.016 ns/op
Iteration  18: 537.372 ns/op
Iteration  19: 547.790 ns/op
Iteration  20: 563.282 ns/op
Iteration  21: 564.065 ns/op
Iteration  22: 550.300 ns/op
Iteration  23: 559.625 ns/op
Iteration  24: 556.051 ns/op
Iteration  25: 551.800 ns/op

Result: 558.604 ±(99.9%) 6.981 ns/op [Average]
  Statistics: (min, avg, max) = (537.372, 558.604, 584.541), stdev = 9.320
  Confidence interval (99.9%): [551.623, 565.586]


# Run progress: 50.00% complete, ETA 00:01:00
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.TaskExecTest.java
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7542 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile
.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 690.069 ns/op
# Warmup Iteration   2: 528.810 ns/op
# Warmup Iteration   3: 552.933 ns/op
# Warmup Iteration   4: 509.330 ns/op
# Warmup Iteration   5: 549.732 ns/op
# Warmup Iteration   6: 563.410 ns/op
# Warmup Iteration   7: 538.632 ns/op
# Warmup Iteration   8: 439.516 ns/op
# Warmup Iteration   9: 430.466 ns/op
# Warmup Iteration  10: 578.849 ns/op
# Warmup Iteration  11: 422.018 ns/op
# Warmup Iteration  12: 437.942 ns/op
# Warmup Iteration  13: 459.192 ns/op
# Warmup Iteration  14: 1600.716 ns/op
# Warmup Iteration  15: 456.912 ns/op
# Warmup Iteration  16: 476.362 ns/op
# Warmup Iteration  17: 433.358 ns/op
# Warmup Iteration  18: 431.879 ns/op
# Warmup Iteration  19: 5080.676 ns/op
# Warmup Iteration  20: 436.658 ns/op
# Warmup Iteration  21: 442.772 ns/op
# Warmup Iteration  22: 432.632 ns/op
# Warmup Iteration  23: 910.838 ns/op
# Warmup Iteration  24: 489.627 ns/op
# Warmup Iteration  25: 506.912 ns/op
Iteration   1: 510.265 ns/op
Iteration   2: 483.254 ns/op
Iteration   3: 1589.483 ns/op
Iteration   4: 467.225 ns/op
Iteration   5: 470.855 ns/op
Iteration   6: 1206.356 ns/op
Iteration   7: 496.835 ns/op
Iteration   8: 508.282 ns/op
Iteration   9: 1062.411 ns/op
Iteration  10: 503.161 ns/op
Iteration  11: 490.954 ns/op
Iteration  12: 2060.382 ns/op
Iteration  13: 503.568 ns/op
Iteration  14: 512.740 ns/op
Iteration  15: 798.950 ns/op
Iteration  16: 495.765 ns/op
Iteration  17: 1311.388 ns/op
Iteration  18: 501.932 ns/op
Iteration  19: 773.368 ns/op
Iteration  20: 475.833 ns/op
Iteration  21: 497.235 ns/op
Iteration  22: 965.159 ns/op
Iteration  23: 496.559 ns/op
Iteration  24: 763.500 ns/op
Iteration  25: 496.305 ns/op
 */

@State(Scope.Benchmark) public class TaskExecTest {
    private static final ConcurrentTaskExecutor<String> TASK_EXECUTOR = new ConcurrentTaskExecutor<>(4);
    private static final TaskExecutor EXECUTOR = TASK_EXECUTOR.scaledThread();
    private static final ExecutorService JAVA = Executors.newFixedThreadPool(4);
    private static final Runnable RUNNABLE = new Runnable() {
        int anInt = 0;

        @Override
        public void run() {
            anInt++;
        }
    };
    @Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
    private int cpuTokens;

    public static void main3(String[] args) {
        ConcurrentTaskExecutor<String> concurrentTaskExecutor = new ConcurrentTaskExecutor<>(4);
        TaskExecutor executor = concurrentTaskExecutor.scaledThread();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        while (true) {
            executor.addTask(runnable);
        }
    }

    public static void main0(String... args) {
        ConcurrentTaskExecutor<String> concurrentTaskExecutor = new ConcurrentTaskExecutor<>(4);
        Collection<TaskExecutor> taskExecutors = concurrentTaskExecutor.threadList();
        for (TaskExecutor taskExecutor : taskExecutors) {
            final String name = taskExecutor.asThread().getName();
            TaskExecutor executor = concurrentTaskExecutor.scaledThread();
            executor.addTask(new Runnable() {
                @Override
                public void run() {
                    System.out.println(name);
                }
            });
            concurrentTaskExecutor.assign(name);
        }
        concurrentTaskExecutor.shutdown();
    }

    //@Benchmark
    //public void scale(Blackhole blackhole) {
    //    blackhole.consume(TASK_EXECUTOR.scaledThread());
    //}

    //@Benchmark
    //public void assign() {
    //TASK_EXECUTOR.assign(EXECUTOR, "Lol");
    //}

    //@Param({ "1", "4", "16", "256"}) private int threads;
    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + TaskExecTest.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS).mode(Mode.AverageTime).warmupIterations(20).warmupTime(
                        TimeValue.milliseconds(10))              // ALLOWED TIME
                .measurementIterations(5).measurementTime(TimeValue.milliseconds(10))         // ALLOWED TIME
                .forks(1)                                           // FORKS
                .verbosity(VerboseMode.SILENT)                      // GRAPH
                .threads(4)                                         // THREADS
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        TASK_EXECUTOR.shutdown();
        JAVA.shutdownNow();
        Benchmarks.chart(Benchmarks.parse(results), "ConcurrentTaskExecutor vs ExecutorService");
    }

    @Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void serviceEquivalent() {
        Blackhole.consumeCPU(cpuTokens);
        TASK_EXECUTOR.execute(RUNNABLE);
    }

    @Benchmark
    public void executorService() {
        Blackhole.consumeCPU(cpuTokens);
        JAVA.execute(RUNNABLE);
    }

    @Benchmark
    public void concurrentTaskExecutor() {
        Blackhole.consumeCPU(cpuTokens);
        EXECUTOR.addTask(RUNNABLE);
    }
}
