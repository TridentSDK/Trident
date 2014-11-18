package net.tridentsdk.impl;

import net.tridentsdk.api.perf.AddTakeQueue;
import net.tridentsdk.api.perf.DelegatedAddTakeQueue;
import net.tridentsdk.api.perf.ReImplLinkedQueue;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/*
# Run progress: 0.00% complete, ETA 00:00:50
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.impl.TaskExecTest.exec
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7535 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 361.778 ns/op
# Warmup Iteration   2: 348.492 ns/op
# Warmup Iteration   3: 335.188 ns/op
# Warmup Iteration   4: 383.734 ns/op
# Warmup Iteration   5: 382.482 ns/op
# Warmup Iteration   6: 421.654 ns/op
# Warmup Iteration   7: 389.149 ns/op
# Warmup Iteration   8: 380.398 ns/op
# Warmup Iteration   9: 353.353 ns/op
# Warmup Iteration  10: 390.800 ns/op
# Warmup Iteration  11: 385.501 ns/op
# Warmup Iteration  12: 377.958 ns/op
# Warmup Iteration  13: 355.122 ns/op
# Warmup Iteration  14: 352.397 ns/op
# Warmup Iteration  15: 393.910 ns/op
# Warmup Iteration  16: 381.003 ns/op
# Warmup Iteration  17: 375.974 ns/op
# Warmup Iteration  18: 373.422 ns/op
# Warmup Iteration  19: 380.850 ns/op
# Warmup Iteration  20: 387.549 ns/op
# Warmup Iteration  21: 505.527 ns/op
# Warmup Iteration  22: 551.361 ns/op
# Warmup Iteration  23: 850.656 ns/op
# Warmup Iteration  24: 536.381 ns/op
# Warmup Iteration  25: 1174.759 ns/op
Iteration   1: 880.716 ns/op
Iteration   2: 605.144 ns/op
Iteration   3: 1119.782 ns/op
Iteration   4: 641.184 ns/op
Iteration   5: 951.436 ns/op
Iteration   6: 665.482 ns/op
Iteration   7: 968.322 ns/op
Iteration   8: 664.995 ns/op
Iteration   9: 888.695 ns/op
Iteration  10: 667.795 ns/op
Iteration  11: 881.844 ns/op
Iteration  12: 691.297 ns/op
Iteration  13: 933.328 ns/op
Iteration  14: 386.154 ns/op
Iteration  15: 613.511 ns/op
Iteration  16: 4240.645 ns/op
Iteration  17: 375.490 ns/op
Iteration  18: 2788.482 ns/op
Iteration  19: 367.908 ns/op
Iteration  20: 2762.270 ns/op
Iteration  21: 355.543 ns/op
Iteration  22: 1815.407 ns/op
Iteration  23: 363.943 ns/op
Iteration  24: 1965.824 ns/op
Iteration  25: 366.282 ns/op

Result: 1078.459 ±(99.9%) 710.051 ns/op [Average]
  Statistics: (min, avg, max) = (355.543, 1078.459, 4240.645), stdev = 947.898
  Confidence interval (99.9%): [368.408, 1788.511]


# Run complete. Total time: 00:01:14

Benchmark                   Mode   Samples        Score  Score error    Units
n.t.s.TaskExecTest.exec     avgt        25     1078.459      710.051    ns/op
 */
public class ReImplTest {
    private static final AddTakeQueue<Object> OBJECTS = new ReImplLinkedQueue<>();
    private static final AddTakeQueue <Object> OBJECT_ADD_TAKE_QUEUE = new DelegatedAddTakeQueue<Object>() {
        @Override
        protected BlockingQueue<Object> delegate() {
            return new LinkedBlockingQueue<>();
        }
    };

    private static final Object OBJECT = new Object();

    public static void main0(String[] args) {
        OBJECTS.add(new Object());
        //OBJECTS.add(new Object());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(OBJECTS.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OBJECTS.add(new Object());
                    }
                }).start();
                try {
                    System.out.println(OBJECTS.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + ReImplTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.MILLISECONDS)
                .mode(Mode.Throughput)
                .warmupIterations(25)
                .measurementIterations(25)
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void areimplAdd() {
        OBJECTS.add(OBJECT);
    }

    @Benchmark
    public void areimplRemove() {
        try {
            OBJECTS.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public void implAdd() {
        OBJECT_ADD_TAKE_QUEUE.add(OBJECT);
    }

    @Benchmark
    public void implRemove() {
        try {
            OBJECT_ADD_TAKE_QUEUE.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
