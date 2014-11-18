package net.tridentsdk.impl;

import net.tridentsdk.api.scheduling.TridentRunnable;
import net.tridentsdk.api.plugin.TridentPlugin;
import net.tridentsdk.api.plugin.annotation.PluginDescription;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/*
# Run progress: 0.00% complete, ETA 00:00:50
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.impl.SchedulerTest.tick
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7540 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 954.107 ns/op
# Warmup Iteration   2: 735.644 ns/op
# Warmup Iteration   3: 783.314 ns/op
# Warmup Iteration   4: 709.969 ns/op
# Warmup Iteration   5: 729.459 ns/op
# Warmup Iteration   6: 744.118 ns/op
# Warmup Iteration   7: 713.706 ns/op
# Warmup Iteration   8: 740.156 ns/op
# Warmup Iteration   9: 759.394 ns/op
# Warmup Iteration  10: 782.898 ns/op
# Warmup Iteration  11: 804.290 ns/op
# Warmup Iteration  12: 746.960 ns/op
# Warmup Iteration  13: 803.499 ns/op
# Warmup Iteration  14: 729.192 ns/op
# Warmup Iteration  15: 700.817 ns/op
# Warmup Iteration  16: 726.183 ns/op
# Warmup Iteration  17: 831.156 ns/op
# Warmup Iteration  18: 726.561 ns/op
# Warmup Iteration  19: 699.661 ns/op
# Warmup Iteration  20: 825.942 ns/op
# Warmup Iteration  21: 861.622 ns/op
# Warmup Iteration  22: 823.657 ns/op
# Warmup Iteration  23: 774.470 ns/op
# Warmup Iteration  24: 674.512 ns/op
# Warmup Iteration  25: 729.422 ns/op
Iteration   1: 699.842 ns/op
Iteration   2: 727.170 ns/op
Iteration   3: 765.262 ns/op
Iteration   4: 758.377 ns/op
Iteration   5: 720.670 ns/op
Iteration   6: 788.388 ns/op
Iteration   7: 839.348 ns/op
Iteration   8: 712.256 ns/op
Iteration   9: 745.712 ns/op
Iteration  10: 740.440 ns/op
Iteration  11: 684.947 ns/op
Iteration  12: 803.585 ns/op
Iteration  13: 762.076 ns/op
Iteration  14: 800.560 ns/op
Iteration  15: 722.222 ns/op
Iteration  16: 794.459 ns/op
Iteration  17: 775.019 ns/op
Iteration  18: 758.636 ns/op
Iteration  19: 3028.849 ns/op
Iteration  20: 769.268 ns/op
Iteration  21: 736.608 ns/op
Iteration  22: 2516.234 ns/op
Iteration  23: 726.281 ns/op
Iteration  24: 1523.716 ns/op
Iteration  25: 676.316 ns/op
 */

/*
CraftBukkit scheduler ticking (100 tasks performing System.out.println("");)
# Run progress: 0.00% complete, ETA 00:00:50
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: com.pm_mc.bandetection.Bench.tick
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 230.228 ns/op
# Warmup Iteration   2: 222.779 ns/op
# Warmup Iteration   3: 233.294 ns/op
# Warmup Iteration   4: 240.464 ns/op
# Warmup Iteration   5: 234.630 ns/op
# Warmup Iteration   6: 231.441 ns/op
# Warmup Iteration   7: 248.108 ns/op
# Warmup Iteration   8: 243.750 ns/op
# Warmup Iteration   9: 247.147 ns/op
# Warmup Iteration  10: 246.898 ns/op
# Warmup Iteration  11: 235.593 ns/op
# Warmup Iteration  12: 240.151 ns/op
# Warmup Iteration  13: 248.003 ns/op
# Warmup Iteration  14: 240.358 ns/op
# Warmup Iteration  15: 196.097 ns/op
# Warmup Iteration  16: 262.766 ns/op
# Warmup Iteration  17: 232.488 ns/op
# Warmup Iteration  18: 249.582 ns/op
# Warmup Iteration  19: 245.064 ns/op
# Warmup Iteration  20: 248.367 ns/op
# Warmup Iteration  21: 236.623 ns/op
# Warmup Iteration  22: 249.857 ns/op
# Warmup Iteration  23: 247.233 ns/op
# Warmup Iteration  24: 258.071 ns/op
# Warmup Iteration  25: 242.321 ns/op
Iteration   1: 240.381 ns/op
Iteration   2: 222.785 ns/op
Iteration   3: 220.152 ns/op
Iteration   4: 247.443 ns/op
Iteration   5: 245.628 ns/op
Iteration   6: 240.036 ns/op
Iteration   7: 244.685 ns/op
Iteration   8: 244.159 ns/op
Iteration   9: 241.201 ns/op
Iteration  10: 252.653 ns/op
Iteration  11: 247.427 ns/op
Iteration  12: 225.500 ns/op
Iteration  13: 229.869 ns/op
Iteration  14: 239.143 ns/op
Iteration  15: 225.088 ns/op
Iteration  16: 246.346 ns/op
Iteration  17: 251.873 ns/op
Iteration  18: 241.026 ns/op
Iteration  19: 256.542 ns/op
Iteration  20: 247.212 ns/op
Iteration  21: 230.969 ns/op
Iteration  22: 236.480 ns/op
Iteration  23: 244.034 ns/op
Iteration  24: 237.303 ns/op
Iteration  25: 248.870 ns/op

Result: 240.272 ±(99.9%) 7.288 ns/op [Average]
  Statistics: (min, avg, max) = (220.152, 240.272, 256.542), stdev = 9.729
  Confidence interval (99.9%): [232.984, 247.560]


# Run complete. Total time: 00:01:00

Benchmark            Mode   Samples        Score  Score error    Units
c.p.b.Bench.tick     avgt        25      240.272        7.288    ns/op
 */
@State(Scope.Benchmark)
public class SchedulerTest {
    private static final TridentScheduler scheduler = new TridentScheduler();

    @Setup
    public void setup() {
        for (int i = 0; i < 100; i++) {
            @PluginDescription(name = "LOLCODE")
            class PluginImpl extends TridentPlugin {
            }

            scheduler.runTaskSyncLater(new PluginImpl(), new TridentRunnable() {
                @Override
                public void run() {
                    System.out.print("");
                }
            }, 20L);
        }
    }

    public static void main(String... args) throws InterruptedException {
        @PluginDescription(name = "LOLCODE")
        class PluginImpl extends TridentPlugin {
        }

        for (int i = 0; i < 1000; i++) {
            scheduler.runTaskAsyncRepeating(new PluginImpl(), new TridentRunnable() {
                @Override
                public void run() {
                    System.out.println("Your mom");
                }
            }, 0L, 42L);
        }

        for (int i = 0; i < 1000000; i++) {
            Thread.sleep(50);
            scheduler.tick();
        }
    }

    public static void main1(String... args) {
        TridentScheduler scheduler = new TridentScheduler();
        while (true) {
            scheduler.tick();
        }
    }

    public static void main8(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + SchedulerTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(25)
                .measurementIterations(25)
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
        scheduler.stop();
    }

    @Benchmark
    public void tick() {
        scheduler.tick();
    }

    public static void main0(String... args) throws InterruptedException {
        TridentScheduler scheduler = new TridentScheduler();
        for (int i = 0; i < 100; i++) {
            @PluginDescription(name = "LOLCODE")
            class PluginImpl extends TridentPlugin {
            }

            final int finalI = i;
            scheduler.runTaskAsyncRepeating(new PluginImpl(), new TridentRunnable() {
                @Override
                public void run() {
                    System.out.println("LOL: " + finalI);
                }
            }, 0L, 20L);
        }
        for (int i = 0; i < 100; i++) {
            Thread.sleep(50);
            scheduler.tick();
        }

        scheduler.stop();
    }
}
