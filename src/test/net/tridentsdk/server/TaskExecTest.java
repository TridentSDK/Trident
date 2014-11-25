package net.tridentsdk.server;

import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/*
Thread-1
Thread-3
Thread-0
Thread-2

Process finished with exit code 0
 */

/*
# Run progress: 0.00% complete, ETA 00:00:50
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.TaskExecTest.exec
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7545 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 569.669 ns/op
# Warmup Iteration   2: 529.240 ns/op
# Warmup Iteration   3: 542.194 ns/op
# Warmup Iteration   4: 464.033 ns/op
# Warmup Iteration   5: 469.193 ns/op
# Warmup Iteration   6: 470.340 ns/op
# Warmup Iteration   7: 496.231 ns/op
# Warmup Iteration   8: 440.094 ns/op
# Warmup Iteration   9: 500.118 ns/op
# Warmup Iteration  10: 532.822 ns/op
# Warmup Iteration  11: 473.105 ns/op
# Warmup Iteration  12: 498.789 ns/op
# Warmup Iteration  13: 530.168 ns/op
# Warmup Iteration  14: 459.882 ns/op
# Warmup Iteration  15: 485.746 ns/op
# Warmup Iteration  16: 492.138 ns/op
# Warmup Iteration  17: 504.958 ns/op
# Warmup Iteration  18: 439.714 ns/op
# Warmup Iteration  19: 573.910 ns/op
# Warmup Iteration  20: 411.165 ns/op
# Warmup Iteration  21: 420.609 ns/op
# Warmup Iteration  22: 520.484 ns/op
# Warmup Iteration  23: 529.989 ns/op
# Warmup Iteration  24: 382.941 ns/op
# Warmup Iteration  25: 472.597 ns/op
Iteration   1: 527.715 ns/op
Iteration   2: 506.930 ns/op
Iteration   3: 502.706 ns/op
Iteration   4: 493.208 ns/op
Iteration   5: 499.672 ns/op
Iteration   6: 406.544 ns/op
Iteration   7: 608.547 ns/op
Iteration   8: 531.963 ns/op
Iteration   9: 534.451 ns/op
Iteration  10: 576.611 ns/op
Iteration  11: 526.561 ns/op
Iteration  12: 466.090 ns/op
Iteration  13: 444.389 ns/op
Iteration  14: 560.751 ns/op
Iteration  15: 477.818 ns/op
Iteration  16: 470.097 ns/op
Iteration  17: 470.094 ns/op
Iteration  18: 492.816 ns/op
Iteration  19: 521.628 ns/op
Iteration  20: 455.511 ns/op
Iteration  21: 519.250 ns/op
Iteration  22: 474.233 ns/op
Iteration  23: 408.087 ns/op
Iteration  24: 453.281 ns/op
Iteration  25: 417.454 ns/op

Result: 493.856 ±(99.9%) 37.584 ns/op [Average]
  Statistics: (min, avg, max) = (406.544, 493.856, 608.547), stdev = 50.174
  Confidence interval (99.9%): [456.272, 531.440]


# Run complete. Total time: 00:01:00

Benchmark                   Mode   Samples        Score  Score error    Units
n.t.s.TaskExecTest.exec     avgt        25      493.856       37.584    ns/op
 */
@State(Scope.Benchmark)
public class TaskExecTest {
    public static void main3(String[] args) {
        ConcurrentTaskExecutor<String> concurrentTaskExecutor = new ConcurrentTaskExecutor<>(4);
        TaskExecutor executor = concurrentTaskExecutor.getScaledThread();
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
            TaskExecutor executor = concurrentTaskExecutor.getScaledThread();
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

    private static final ConcurrentTaskExecutor<String> TASK_EXECUTOR = new ConcurrentTaskExecutor<>(4);
    private static final TaskExecutor EXECUTOR = TASK_EXECUTOR.getScaledThread();

    private static final Runnable RUNNABLE = new Runnable() {
        int anInt = 0;
        @Override
        public void run() {
            anInt++;
        }
    };

    //@Param({ "1", "4", "16", "256"}) private int threads;
    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + TaskExecTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(25)
                .measurementIterations(25)
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
        TASK_EXECUTOR.shutdown();
    }

    //@Benchmark
    //public void scale(Blackhole blackhole) {
    //    blackhole.consume(TASK_EXECUTOR.getScaledThread());
    //}

    //@Benchmark
    //public void assign() {
        //TASK_EXECUTOR.assign(EXECUTOR, "Lol");
    //}

    @Benchmark
    public void exec() {
        EXECUTOR.addTask(RUNNABLE);
    }
}
