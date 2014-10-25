package test.net.tridentsdk.server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.tridentsdk.api.event.EventManager;
import net.tridentsdk.api.event.Listener;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/*
# Run progress: 0.00% complete, ETA 00:01:00
# Warmup: 10 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 10 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.EventBusPerformance.AregisterEb
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7538 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 6778.824 ns/op
# Warmup Iteration   2: 4500.257 ns/op
# Warmup Iteration   3: 4312.210 ns/op
# Warmup Iteration   4: 4375.591 ns/op
# Warmup Iteration   5: 4327.082 ns/op
# Warmup Iteration   6: 4230.110 ns/op
# Warmup Iteration   7: 4239.154 ns/op
# Warmup Iteration   8: 4245.635 ns/op
# Warmup Iteration   9: 4428.637 ns/op
# Warmup Iteration  10: 4319.250 ns/op
Iteration   1: 4267.797 ns/op
Iteration   2: 4265.144 ns/op
Iteration   3: 4345.997 ns/op
Iteration   4: 4377.951 ns/op
Iteration   5: 4473.355 ns/op

Result: 4346.049 ±(99.9%) 332.884 ns/op [Average]
  Statistics: (min, avg, max) = (4265.144, 4346.049, 4473.355), stdev = 86.449
  Confidence interval (99.9%): [4013.165, 4678.932]


# Run progress: 25.00% complete, ETA 00:00:55
# Warmup: 10 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 10 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.EventBusPerformance.AregisterEm
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7538 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: <failure>

java.lang.reflect.InvocationTargetException
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at org.openjdk.jmh.runner.LoopBenchmarkHandler$BenchmarkTask.call(LoopBenchmarkHandler.java:203)
    at org.openjdk.jmh.runner.LoopBenchmarkHandler$BenchmarkTask.call(LoopBenchmarkHandler.java:185)
    at java.util.concurrent.FutureTask.run(FutureTask.java:262)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
    at java.lang.Thread.run(Thread.java:745)
Caused by: java.lang.RuntimeException: Class cannot be created (the no-arg constructor is private): net.tridentsdk.server.EventBusPerformance$EventListener
    at com.esotericsoftware.reflectasm.ConstructorAccess.get(ConstructorAccess.java:59)
    at net.tridentsdk.api.reflect.FastClass.<init>(FastClass.java:44)
    at net.tridentsdk.api.reflect.FastClass.get(FastClass.java:48)
    at net.tridentsdk.api.event.EventManager.registerListener(EventManager.java:83)
    at net.tridentsdk.server.EventBusPerformance.AregisterEm(EventBusPerformance.java:46)
    at net.tridentsdk.server.generated.EventBusPerformance_AregisterEm.AregisterEm_AverageTime(EventBusPerformance_AregisterEm.java:124)
    ... 10 more



# Run progress: 50.00% complete, ETA 00:00:20
# Warmup: 10 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 10 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.EventBusPerformance.BdoCallEb
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7538 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 14579.668 ns/op
# Warmup Iteration   2: 6944.432 ns/op
# Warmup Iteration   3: 6664.215 ns/op
# Warmup Iteration   4: 6760.369 ns/op
# Warmup Iteration   5: 6530.998 ns/op
# Warmup Iteration   6: 6773.945 ns/op
# Warmup Iteration   7: 6602.602 ns/op
# Warmup Iteration   8: 6585.164 ns/op
# Warmup Iteration   9: 6661.623 ns/op
# Warmup Iteration  10: 6763.851 ns/op
Iteration   1: 6710.809 ns/op
Iteration   2: 6675.678 ns/op
Iteration   3: 6805.387 ns/op
Iteration   4: 6827.599 ns/op
Iteration   5: 6858.508 ns/op

Result: 6775.596 ±(99.9%) 302.264 ns/op [Average]
  Statistics: (min, avg, max) = (6675.678, 6775.596, 6858.508), stdev = 78.497
  Confidence interval (99.9%): [6473.332, 7077.860]


# Run progress: 75.00% complete, ETA 00:00:12
# Warmup: 10 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 10 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.EventBusPerformance.BdoCallEm
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7538 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 3.713 ns/op
# Warmup Iteration   2: 3.831 ns/op
# Warmup Iteration   3: 3.749 ns/op
# Warmup Iteration   4: 3.769 ns/op
# Warmup Iteration   5: 3.728 ns/op
# Warmup Iteration   6: 3.745 ns/op
# Warmup Iteration   7: 3.808 ns/op
# Warmup Iteration   8: 3.779 ns/op
# Warmup Iteration   9: 3.824 ns/op
# Warmup Iteration  10: 3.798 ns/op
Iteration   1: 3.798 ns/op
Iteration   2: 3.796 ns/op
Iteration   3: 3.762 ns/op
Iteration   4: 3.765 ns/op
Iteration   5: 3.845 ns/op

Result: 3.793 ±(99.9%) 0.128 ns/op [Average]
  Statistics: (min, avg, max) = (3.762, 3.793, 3.845), stdev = 0.033
  Confidence interval (99.9%): [3.665, 3.922]


# Run complete. Total time: 00:00:56

Benchmark                                 Mode   Samples        Score  Score error    Units
n.t.s.EventBusPerformance.AregisterEb     avgt         5     4346.049      332.884    ns/op
n.t.s.EventBusPerformance.BdoCallEb       avgt         5     6775.596      302.264    ns/op
n.t.s.EventBusPerformance.BdoCallEm       avgt         5        3.793        0.128    ns/op
 */
public class EventBusPerformance {
    private static final EventBus EVENT_BUS = new EventBus();
    private static final EventManager EVENT_MANAGER = new EventManager();

    private static final EventHandler HANDLER = new EventHandler();
    private static final Listener LISTENER = new EventListener();

    private static final net.tridentsdk.api.event.Event EVENT = new Event();

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + EventBusPerformance.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(10)
                .measurementIterations(5)
                .forks(1)
                .threads(10)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void AregisterEb() {
        EVENT_BUS.register(HANDLER);
    }

    @Benchmark
    public void AregisterEm() {
        EVENT_MANAGER.registerListener(LISTENER);
    }

    @Benchmark
    public void BdoCallEb() {
        EVENT_BUS.post(EVENT);
    }

    @Benchmark public void BdoCallEm() {
        EVENT_MANAGER.call(EVENT);
    }

    private static class Event extends net.tridentsdk.api.event.Event {
    }

    private static class EventHandler {
        @Subscribe
        public void handle(Event event) {
        }
    }

    private static class EventListener implements Listener {
        @net.tridentsdk.api.event.EventHandler
        public void onEvent(Event event) {
        }
    }
}
