package net.tridentsdk.server;

import net.tridentsdk.api.scheduling.TridentRunnable;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.plugin.annotation.PluginDescription;
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
# Benchmark: net.tridentsdk.server.SchedulerTest.tick
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 704.241 ns/op
# Warmup Iteration   2: 594.314 ns/op
# Warmup Iteration   3: 512.624 ns/op
# Warmup Iteration   4: 539.174 ns/op
# Warmup Iteration   5: 591.239 ns/op
# Warmup Iteration   6: 520.194 ns/op
# Warmup Iteration   7: 482.784 ns/op
# Warmup Iteration   8: 557.652 ns/op
# Warmup Iteration   9: 542.271 ns/op
# Warmup Iteration  10: 566.883 ns/op
# Warmup Iteration  11: 508.995 ns/op
# Warmup Iteration  12: 552.878 ns/op
# Warmup Iteration  13: 486.169 ns/op
# Warmup Iteration  14: 523.269 ns/op
# Warmup Iteration  15: 492.847 ns/op
# Warmup Iteration  16: 477.326 ns/op
# Warmup Iteration  17: 484.904 ns/op
# Warmup Iteration  18: 522.773 ns/op
# Warmup Iteration  19: 486.763 ns/op
# Warmup Iteration  20: 519.879 ns/op
# Warmup Iteration  21: 466.321 ns/op
# Warmup Iteration  22: 489.865 ns/op
# Warmup Iteration  23: 536.677 ns/op
# Warmup Iteration  24: 492.363 ns/op
# Warmup Iteration  25: 500.912 ns/op
Iteration   1: 472.686 ns/op
Iteration   2: 569.529 ns/op
Iteration   3: 526.067 ns/op
Iteration   4: 492.186 ns/op
Iteration   5: 444.862 ns/op
Iteration   6: 522.097 ns/op
Iteration   7: 508.776 ns/op
Iteration   8: 513.399 ns/op
Iteration   9: 482.863 ns/op
Iteration  10: 545.456 ns/op
Iteration  11: 522.218 ns/op
Iteration  12: 525.920 ns/op
Iteration  13: 505.476 ns/op
Iteration  14: 505.253 ns/op
Iteration  15: 497.361 ns/op
Iteration  16: 487.234 ns/op
Iteration  17: 545.665 ns/op
Iteration  18: 490.612 ns/op
Iteration  19: 525.880 ns/op
Iteration  20: 480.943 ns/op
Iteration  21: 464.872 ns/op
Iteration  22: 521.951 ns/op
Iteration  23: 487.223 ns/op
Iteration  24: 502.040 ns/op
Iteration  25: 518.449 ns/op
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

    public static void main1(String[] args) {
        TridentScheduler scheduler = new TridentScheduler();
        while (true) {
            scheduler.tick();
        }
    }

    public static void main(String... args) throws RunnerException {
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
