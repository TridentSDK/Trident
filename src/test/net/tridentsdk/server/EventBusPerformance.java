package net.tridentsdk.server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.tridentsdk.api.event.EventManager;
import net.tridentsdk.api.event.Listener;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/*
# Run progress: 0.00% complete, ETA 00:00:30
# Warmup: 10 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.EventBusPerformance.AregisterEm
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 5028.094 ns/op
# Warmup Iteration   2: 4338.142 ns/op
# Warmup Iteration   3: 3858.112 ns/op
# Warmup Iteration   4: 3682.948 ns/op
# Warmup Iteration   5: 3701.799 ns/op
# Warmup Iteration   6: 14153.180 ns/op
# Warmup Iteration   7: 4198.486 ns/op
# Warmup Iteration   8: 3349.843 ns/op
# Warmup Iteration   9: 4256.990 ns/op
# Warmup Iteration  10: 3390.103 ns/op
Iteration   1: 15996.866 ns/op
Iteration   2: 3438.164 ns/op
Iteration   3: 3393.053 ns/op
Iteration   4: 5156.833 ns/op
Iteration   5: 3418.863 ns/op

Result: 6280.756 ±(99.9%) 21115.049 ns/op [Average]
  Statistics: (min, avg, max) = (3393.053, 6280.756, 15996.866), stdev = 5483.511
  Confidence interval (99.9%): [-14834.293, 27395.805]


# Run progress: 50.00% complete, ETA 00:00:25
# Warmup: 10 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.EventBusPerformance.BdoCallEm
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 35.368 ns/op
# Warmup Iteration   2: 31.647 ns/op
# Warmup Iteration   3: 28.225 ns/op
# Warmup Iteration   4: 28.583 ns/op
# Warmup Iteration   5: 28.629 ns/op
# Warmup Iteration   6: 28.728 ns/op
# Warmup Iteration   7: 28.692 ns/op
# Warmup Iteration   8: 28.732 ns/op
# Warmup Iteration   9: 28.900 ns/op
# Warmup Iteration  10: 29.151 ns/op
Iteration   1: 29.080 ns/op
Iteration   2: 28.933 ns/op
Iteration   3: 28.798 ns/op
Iteration   4: 28.565 ns/op
Iteration   5: 29.035 ns/op

Result: 28.882 ±(99.9%) 0.800 ns/op [Average]
  Statistics: (min, avg, max) = (28.565, 28.882, 29.080), stdev = 0.208
  Confidence interval (99.9%): [28.082, 29.682]


# Run complete. Total time: 00:00:43

Benchmark                                 Mode   Samples        Score  Score error    Units
n.t.s.EventBusPerformance.AregisterEm     avgt         5     6280.756    21115.049    ns/op
n.t.s.EventBusPerformance.BdoCallEm       avgt         5       28.882        0.800    ns/op
*/
@State(Scope.Benchmark)
public class EventBusPerformance {
    private static final EventBus EVENT_BUS = new EventBus();
    private static final EventManager EVENT_MANAGER = new EventManager();

    private static final EventHandler HANDLER = new EventHandler();
    private static final Listener LISTENER = new EventListener();

    private static final net.tridentsdk.api.event.Event EVENT = new Event();

    public static void main0(String[] args) {
        while (true) {
            EVENT_MANAGER.registerListener(LISTENER);
            // EVENT_MANAGER.call(EVENT);
        }
    }

    @Setup
    public void setUp() {
        EVENT_MANAGER.registerListener(LISTENER);
    }

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + EventBusPerformance.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(10)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    //@Benchmark
    public void AregisterEb() {
        EVENT_BUS.register(HANDLER);
    }

    @Benchmark
    public void AregisterEm() {
        EVENT_MANAGER.registerListener(LISTENER);
    }

    //@Benchmark
    public void BdoCallEb() {
        EVENT_BUS.post(EVENT);
    }

    @Benchmark
    public void BdoCallEm() {
        EVENT_MANAGER.call(EVENT);
    }

    public static class Event extends net.tridentsdk.api.event.Event {
    }

    private static class EventHandler {
        @Subscribe
        public void handle(Event event) {
        }
    }

    public static class EventListener implements Listener {
        public void onEvent(EventBusPerformance.Event event) {
            //System.out.println("lol");
        }
    }
}
