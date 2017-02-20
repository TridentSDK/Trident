package net.tridentsdk.server;

import net.tridentsdk.base.Substance;
import net.tridentsdk.server.world.TridentBlock;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.util.Misc;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/*
AGENTTROLL:
# Run progress: 0.00% complete, ETA 00:00:50
# Warmup: 20 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.BlockBenchmark.testRead
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7541 -Didea.launcher.bin.path=/usr/local/lib/IntelliJ-IDEA/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 979337.358 ns/op
# Warmup Iteration   2: 147938.307 ns/op
# Warmup Iteration   3: 196760.948 ns/op
# Warmup Iteration   4: 30220.073 ns/op
# Warmup Iteration   5: 2145.075 ns/op
# Warmup Iteration   6: 1817.127 ns/op
# Warmup Iteration   7: 1860.342 ns/op
# Warmup Iteration   8: 1857.835 ns/op
# Warmup Iteration   9: 1848.804 ns/op
# Warmup Iteration  10: 1887.457 ns/op
# Warmup Iteration  11: 1839.159 ns/op
# Warmup Iteration  12: 1922.204 ns/op
# Warmup Iteration  13: 1847.379 ns/op
# Warmup Iteration  14: 1857.055 ns/op
# Warmup Iteration  15: 1865.706 ns/op
# Warmup Iteration  16: 1852.171 ns/op
# Warmup Iteration  17: 1896.486 ns/op
# Warmup Iteration  18: 1856.312 ns/op
# Warmup Iteration  19: 1861.494 ns/op
# Warmup Iteration  20: 1870.481 ns/op
Iteration   1: 1828.225 ns/op
Iteration   2: 1854.067 ns/op
Iteration   3: 1829.432 ns/op
Iteration   4: 1858.773 ns/op
Iteration   5: 1886.571 ns/op

Result: 1851.414 ±(99.9%) 92.699 ns/op [Average]
  Statistics: (min, avg, max) = (1828.225, 1851.414, 1886.571), stdev = 24.074
  Confidence interval (99.9%): [1758.715, 1944.112]


# Run progress: 50.00% complete, ETA 00:00:55
# Warmup: 20 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.BlockBenchmark.testWrite
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7541 -Didea.launcher.bin.path=/usr/local/lib/IntelliJ-IDEA/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 325138.451 ns/op
# Warmup Iteration   2: 417340.501 ns/op
# Warmup Iteration   3: 154689.558 ns/op
# Warmup Iteration   4: 86473.534 ns/op
# Warmup Iteration   5: 67847.610 ns/op
# Warmup Iteration   6: 2747.028 ns/op
# Warmup Iteration   7: 1828.321 ns/op
# Warmup Iteration   8: 1831.568 ns/op
# Warmup Iteration   9: 1810.503 ns/op
# Warmup Iteration  10: 1795.792 ns/op
# Warmup Iteration  11: 1870.198 ns/op
# Warmup Iteration  12: 1829.132 ns/op
# Warmup Iteration  13: 1816.830 ns/op
# Warmup Iteration  14: 1806.147 ns/op
# Warmup Iteration  15: 1831.269 ns/op
# Warmup Iteration  16: 1842.629 ns/op
# Warmup Iteration  17: 1831.351 ns/op
# Warmup Iteration  18: 1817.871 ns/op
# Warmup Iteration  19: 1802.796 ns/op
# Warmup Iteration  20: 1806.449 ns/op
Iteration   1: 1831.533 ns/op
Iteration   2: 1801.125 ns/op
Iteration   3: 1819.242 ns/op
Iteration   4: 1781.216 ns/op
Iteration   5: 1817.367 ns/op

Result: 1810.097 ±(99.9%) 74.830 ns/op [Average]
  Statistics: (min, avg, max) = (1781.216, 1810.097, 1831.533), stdev = 19.433
  Confidence interval (99.9%): [1735.267, 1884.927]


# Run complete. Total time: 00:01:45

Benchmark                          Mode   Samples        Score  Score error    Units
n.t.s.BlockBenchmark.testRead      avgt         5     1851.414       92.699    ns/op
n.t.s.BlockBenchmark.testWrite     avgt         5     1810.097       74.830    ns/op

VILSOL:
# Run progress: 0.00% complete, ETA 00:00:50
# Warmup: 20 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.BlockBenchmark.testRead
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7540 -Didea.launcher.bin.path=/usr/local/lib/IntelliJ-IDEA/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 160.585 ns/op
# Warmup Iteration   2: 161.269 ns/op
# Warmup Iteration   3: 148.613 ns/op
# Warmup Iteration   4: 154.589 ns/op
# Warmup Iteration   5: 148.356 ns/op
# Warmup Iteration   6: 153.741 ns/op
# Warmup Iteration   7: 157.224 ns/op
# Warmup Iteration   8: 149.892 ns/op
# Warmup Iteration   9: 156.030 ns/op
# Warmup Iteration  10: 148.998 ns/op
# Warmup Iteration  11: 152.246 ns/op
# Warmup Iteration  12: 154.295 ns/op
# Warmup Iteration  13: 149.815 ns/op
# Warmup Iteration  14: 153.125 ns/op
# Warmup Iteration  15: 150.117 ns/op
# Warmup Iteration  16: 153.978 ns/op
# Warmup Iteration  17: 156.571 ns/op
# Warmup Iteration  18: 177.610 ns/op
# Warmup Iteration  19: 159.905 ns/op
# Warmup Iteration  20: 149.247 ns/op
Iteration   1: 151.224 ns/op
Iteration   2: 156.318 ns/op
Iteration   3: 150.774 ns/op
Iteration   4: 157.547 ns/op
Iteration   5: 149.010 ns/op

Result: 152.975 ±(99.9%) 14.371 ns/op [Average]
  Statistics: (min, avg, max) = (149.010, 152.975, 157.547), stdev = 3.732
  Confidence interval (99.9%): [138.604, 167.346]


# Run progress: 50.00% complete, ETA 00:00:55
# Warmup: 20 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.BlockBenchmark.testWrite
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7540 -Didea.launcher.bin.path=/usr/local/lib/IntelliJ-IDEA/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 2171.686 ns/op
# Warmup Iteration   2: 5186.809 ns/op
# Warmup Iteration   3: 2072.695 ns/op
# Warmup Iteration   4: 2084.410 ns/op
# Warmup Iteration   5: 2086.619 ns/op
# Warmup Iteration   6: 2016.155 ns/op
# Warmup Iteration   7: 2068.729 ns/op
# Warmup Iteration   8: 2014.698 ns/op
# Warmup Iteration   9: 2056.359 ns/op
# Warmup Iteration  10: 2090.780 ns/op
# Warmup Iteration  11: 2033.207 ns/op
# Warmup Iteration  12: 2112.924 ns/op
# Warmup Iteration  13: 2032.418 ns/op
# Warmup Iteration  14: 2083.473 ns/op
# Warmup Iteration  15: 128133.977 ns/op
# Warmup Iteration  16: 2109.099 ns/op
# Warmup Iteration  17: 2132.186 ns/op
# Warmup Iteration  18: 2053.429 ns/op
# Warmup Iteration  19: 2068.741 ns/op
# Warmup Iteration  20: 2087.110 ns/op
Iteration   1: 2061.840 ns/op
Iteration   2: 2100.422 ns/op
Iteration   3: 2052.497 ns/op
Iteration   4: 2057.196 ns/op
Iteration   5: 2080.323 ns/op

Result: 2070.455 ±(99.9%) 76.236 ns/op [Average]
  Statistics: (min, avg, max) = (2052.497, 2070.455, 2100.422), stdev = 19.798
  Confidence interval (99.9%): [1994.219, 2146.692]


# Run complete. Total time: 00:02:04

Benchmark                          Mode   Samples        Score  Score error    Units
n.t.s.BlockBenchmark.testRead      avgt         5      152.975       14.371    ns/op
n.t.s.BlockBenchmark.testWrite     avgt         5     2070.455       76.236    ns/op
 */
@State(Scope.Benchmark)
public class BlockBenchmark {
    private static final TridentBlock[] blocks = new TridentBlock[16777216];

    static {
        TridentWorld world = new TridentWorld("world", Misc.HOME_PATH.resolve("world"));
        ThreadLocalRandom current = ThreadLocalRandom.current();
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = (TridentBlock) world.blockAt(current.nextInt(4096), 3, current.nextInt(4096));
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(".*" + BlockBenchmark.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(20)
                .measurementIterations(5)
                .forks(1)
                .threads(4)
                .build();

        new Runner(options).run();
    }

    @Benchmark
    public void testWrite() {
        int idx = ThreadLocalRandom.current().nextInt(blocks.length);
        TridentBlock block = blocks[idx];
        block.setSubstance(Substance.DIRT);
    }

    @Benchmark
    public void testRead(Blackhole bh) {
        int idx = ThreadLocalRandom.current().nextInt(blocks.length);
        TridentBlock block = blocks[idx];
        bh.consume(block.getSubstance());
    }
}