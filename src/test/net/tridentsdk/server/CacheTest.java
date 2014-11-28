package net.tridentsdk.server;

import net.tridentsdk.server.threads.ConcurrentCache;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/*
# Run progress: 0.00% complete, ETA 00:00:50
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.CacheTest.retrieve
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 33.346 ns/op
# Warmup Iteration   2: 25.974 ns/op
# Warmup Iteration   3: 23.145 ns/op
# Warmup Iteration   4: 24.299 ns/op
# Warmup Iteration   5: 24.553 ns/op
# Warmup Iteration   6: 21.989 ns/op
# Warmup Iteration   7: 23.591 ns/op
# Warmup Iteration   8: 23.573 ns/op
# Warmup Iteration   9: 22.535 ns/op
# Warmup Iteration  10: 24.726 ns/op
# Warmup Iteration  11: 22.924 ns/op
# Warmup Iteration  12: 25.210 ns/op
# Warmup Iteration  13: 24.314 ns/op
# Warmup Iteration  14: 23.869 ns/op
# Warmup Iteration  15: 22.497 ns/op
# Warmup Iteration  16: 23.176 ns/op
# Warmup Iteration  17: 24.307 ns/op
# Warmup Iteration  18: 22.727 ns/op
# Warmup Iteration  19: 22.615 ns/op
# Warmup Iteration  20: 23.700 ns/op
# Warmup Iteration  21: 25.010 ns/op
# Warmup Iteration  22: 22.989 ns/op
# Warmup Iteration  23: 23.994 ns/op
# Warmup Iteration  24: 25.053 ns/op
# Warmup Iteration  25: 22.764 ns/op
Iteration   1: 22.099 ns/op
Iteration   2: 25.071 ns/op
Iteration   3: 24.438 ns/op
Iteration   4: 21.341 ns/op
Iteration   5: 21.751 ns/op
Iteration   6: 21.839 ns/op
Iteration   7: 21.997 ns/op
Iteration   8: 20.791 ns/op
Iteration   9: 20.856 ns/op
Iteration  10: 21.385 ns/op
Iteration  11: 20.929 ns/op
Iteration  12: 20.759 ns/op
Iteration  13: 20.776 ns/op
Iteration  14: 22.983 ns/op
Iteration  15: 21.095 ns/op
Iteration  16: 21.146 ns/op
Iteration  17: 20.768 ns/op
Iteration  18: 21.029 ns/op
Iteration  19: 21.194 ns/op
Iteration  20: 21.033 ns/op
Iteration  21: 20.874 ns/op
Iteration  22: 21.330 ns/op
Iteration  23: 21.330 ns/op
Iteration  24: 20.677 ns/op
Iteration  25: 20.808 ns/op

Result: 21.532 ±(99.9%) 0.833 ns/op [Average]
  Statistics: (min, avg, max) = (20.677, 21.532, 25.071), stdev = 1.112
  Confidence interval (99.9%): [20.699, 22.365]


# Run complete. Total time: 00:01:00

Benchmark                    Mode   Samples        Score  Score error    Units
n.t.s.CacheTest.retrieve     avgt        25       21.532        0.833    ns/op
 */
public class CacheTest {
    private static final ConcurrentCache<Object, Object> CACHE = new ConcurrentCache<>();

    private static final Object key = new Object();
    private static final Callable<Object> CALLABLE = new Callable<Object>() {
        @Override
        public Object call() throws Exception {
            return "LOL";
        }
    };

    public static void main0(String[] args) {
        CACHE.retrieve(key, CALLABLE);
        System.out.println(CACHE.remove(key));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + CacheTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(25)
                .measurementIterations(25)
                .forks(1)
                .threads(16)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void retrieve(Blackhole bh) {
        bh.consume(CACHE.retrieve(key, CALLABLE));
    }
}
