package net.tridentsdk.server;

import com.google.common.collect.Sets;
import io.netty.util.internal.ConcurrentSet;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/*
# Run progress: 0.00% complete, ETA 00:03:20
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.CHMTest.CHMPut
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 203.231 ns/op
# Warmup Iteration   2: 224.174 ns/op
# Warmup Iteration   3: 194.532 ns/op
# Warmup Iteration   4: 176.423 ns/op
# Warmup Iteration   5: 201.431 ns/op
# Warmup Iteration   6: 196.027 ns/op
# Warmup Iteration   7: 175.192 ns/op
# Warmup Iteration   8: 196.618 ns/op
# Warmup Iteration   9: 195.558 ns/op
# Warmup Iteration  10: 196.401 ns/op
# Warmup Iteration  11: 192.543 ns/op
# Warmup Iteration  12: 190.847 ns/op
# Warmup Iteration  13: 205.442 ns/op
# Warmup Iteration  14: 196.033 ns/op
# Warmup Iteration  15: 183.810 ns/op
# Warmup Iteration  16: 189.177 ns/op
# Warmup Iteration  17: 193.165 ns/op
# Warmup Iteration  18: 182.265 ns/op
# Warmup Iteration  19: 177.485 ns/op
# Warmup Iteration  20: 191.995 ns/op
# Warmup Iteration  21: 176.599 ns/op
# Warmup Iteration  22: 177.730 ns/op
# Warmup Iteration  23: 179.539 ns/op
# Warmup Iteration  24: 194.055 ns/op
# Warmup Iteration  25: 188.944 ns/op
Iteration   1: 196.785 ns/op
Iteration   2: 198.733 ns/op
Iteration   3: 190.514 ns/op
Iteration   4: 184.714 ns/op
Iteration   5: 184.729 ns/op
Iteration   6: 174.981 ns/op
Iteration   7: 194.215 ns/op
Iteration   8: 189.342 ns/op
Iteration   9: 190.607 ns/op
Iteration  10: 170.458 ns/op
Iteration  11: 178.088 ns/op
Iteration  12: 187.926 ns/op
Iteration  13: 194.330 ns/op
Iteration  14: 188.246 ns/op
Iteration  15: 190.982 ns/op
Iteration  16: 173.882 ns/op
Iteration  17: 176.428 ns/op
Iteration  18: 192.715 ns/op
Iteration  19: 193.465 ns/op
Iteration  20: 190.697 ns/op
Iteration  21: 197.647 ns/op
Iteration  22: 196.909 ns/op
Iteration  23: 189.495 ns/op
Iteration  24: 195.756 ns/op
Iteration  25: 200.358 ns/op

Result: 188.880 ±(99.9%) 6.216 ns/op [Average]
  Statistics: (min, avg, max) = (170.458, 188.880, 200.358), stdev = 8.298
  Confidence interval (99.9%): [182.664, 195.096]


# Run progress: 25.00% complete, ETA 00:03:02
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.CHMTest.CHMRemove
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 23.663 ns/op
# Warmup Iteration   2: 17.808 ns/op
# Warmup Iteration   3: 16.944 ns/op
# Warmup Iteration   4: 17.597 ns/op
# Warmup Iteration   5: 14.708 ns/op
# Warmup Iteration   6: 15.030 ns/op
# Warmup Iteration   7: 15.034 ns/op
# Warmup Iteration   8: 16.269 ns/op
# Warmup Iteration   9: 14.622 ns/op
# Warmup Iteration  10: 16.458 ns/op
# Warmup Iteration  11: 15.799 ns/op
# Warmup Iteration  12: 14.789 ns/op
# Warmup Iteration  13: 14.169 ns/op
# Warmup Iteration  14: 14.572 ns/op
# Warmup Iteration  15: 14.041 ns/op
# Warmup Iteration  16: 11.824 ns/op
# Warmup Iteration  17: 13.282 ns/op
# Warmup Iteration  18: 12.314 ns/op
# Warmup Iteration  19: 11.906 ns/op
# Warmup Iteration  20: 15.190 ns/op
# Warmup Iteration  21: 13.314 ns/op
# Warmup Iteration  22: 15.512 ns/op
# Warmup Iteration  23: 16.850 ns/op
# Warmup Iteration  24: 15.629 ns/op
# Warmup Iteration  25: 14.080 ns/op
Iteration   1: 17.205 ns/op
Iteration   2: 14.691 ns/op
Iteration   3: 13.440 ns/op
Iteration   4: 14.350 ns/op
Iteration   5: 16.485 ns/op
Iteration   6: 14.906 ns/op
Iteration   7: 16.528 ns/op
Iteration   8: 16.219 ns/op
Iteration   9: 13.087 ns/op
Iteration  10: 12.411 ns/op
Iteration  11: 14.819 ns/op
Iteration  12: 12.358 ns/op
Iteration  13: 14.185 ns/op
Iteration  14: 14.817 ns/op
Iteration  15: 14.335 ns/op
Iteration  16: 13.274 ns/op
Iteration  17: 12.892 ns/op
Iteration  18: 14.057 ns/op
Iteration  19: 15.344 ns/op
Iteration  20: 17.650 ns/op
Iteration  21: 16.379 ns/op
Iteration  22: 15.369 ns/op
Iteration  23: 13.943 ns/op
Iteration  24: 15.502 ns/op
Iteration  25: 13.717 ns/op

Result: 14.718 ±(99.9%) 1.091 ns/op [Average]
  Statistics: (min, avg, max) = (12.358, 14.718, 17.650), stdev = 1.456
  Confidence interval (99.9%): [13.628, 15.809]


# Run progress: 50.00% complete, ETA 00:02:01
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.CHMTest.v8CHMPut
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 181.013 ns/op
# Warmup Iteration   2: 175.399 ns/op
# Warmup Iteration   3: 161.583 ns/op
# Warmup Iteration   4: 151.350 ns/op
# Warmup Iteration   5: 1677162927.500 ns/op
# Warmup Iteration   6: 140.928 ns/op
# Warmup Iteration   7: 149.612 ns/op
# Warmup Iteration   8: 152.505 ns/op
# Warmup Iteration   9: 165.376 ns/op
# Warmup Iteration  10: 148.308 ns/op
# Warmup Iteration  11: 161.405 ns/op
# Warmup Iteration  12: 159.796 ns/op
# Warmup Iteration  13: 163.172 ns/op
# Warmup Iteration  14: 140.934 ns/op
# Warmup Iteration  15: 189.205 ns/op
# Warmup Iteration  16: 153.350 ns/op
# Warmup Iteration  17: 163.727 ns/op
# Warmup Iteration  18: 160.596 ns/op
# Warmup Iteration  19: 170.222 ns/op
# Warmup Iteration  20: 157.165 ns/op
# Warmup Iteration  21: 143.011 ns/op
# Warmup Iteration  22: 146.037 ns/op
# Warmup Iteration  23: 144.025 ns/op
# Warmup Iteration  24: 149.316 ns/op
# Warmup Iteration  25: 159.168 ns/op
Iteration   1: 148.167 ns/op
Iteration   2: 156.766 ns/op
Iteration   3: 159.083 ns/op
Iteration   4: 158.052 ns/op
Iteration   5: 143.763 ns/op
Iteration   6: 155.730 ns/op
Iteration   7: 157.642 ns/op
Iteration   8: 165.106 ns/op
Iteration   9: 147.515 ns/op
Iteration  10: 152.657 ns/op
Iteration  11: 156.719 ns/op
Iteration  12: 148.706 ns/op
Iteration  13: 147.277 ns/op
Iteration  14: 151.364 ns/op
Iteration  15: 149.861 ns/op
Iteration  16: 142.924 ns/op
Iteration  17: 145.598 ns/op
Iteration  18: 148.930 ns/op
Iteration  19: 149.270 ns/op
Iteration  20: 144.765 ns/op
Iteration  21: 148.998 ns/op
Iteration  22: 146.636 ns/op
Iteration  23: 146.509 ns/op
Iteration  24: 144.935 ns/op
Iteration  25: 145.664 ns/op

Result: 150.505 ±(99.9%) 4.285 ns/op [Average]
  Statistics: (min, avg, max) = (142.924, 150.505, 165.106), stdev = 5.720
  Confidence interval (99.9%): [146.221, 154.790]


# Run progress: 75.00% complete, ETA 00:01:02
# Warmup: 25 iterations, 1 s each
# Measurement: 25 iterations, 1 s each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.CHMTest.v8CHMRemove
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 17.277 ns/op
# Warmup Iteration   2: 10.271 ns/op
# Warmup Iteration   3: 9.629 ns/op
# Warmup Iteration   4: 8.992 ns/op
# Warmup Iteration   5: 11.345 ns/op
# Warmup Iteration   6: 11.066 ns/op
# Warmup Iteration   7: 11.225 ns/op
# Warmup Iteration   8: 9.575 ns/op
# Warmup Iteration   9: 11.280 ns/op
# Warmup Iteration  10: 10.494 ns/op
# Warmup Iteration  11: 10.170 ns/op
# Warmup Iteration  12: 10.799 ns/op
# Warmup Iteration  13: 10.922 ns/op
# Warmup Iteration  14: 10.299 ns/op
# Warmup Iteration  15: 10.630 ns/op
# Warmup Iteration  16: 10.073 ns/op
# Warmup Iteration  17: 10.659 ns/op
# Warmup Iteration  18: 10.888 ns/op
# Warmup Iteration  19: 11.520 ns/op
# Warmup Iteration  20: 11.196 ns/op
# Warmup Iteration  21: 10.688 ns/op
# Warmup Iteration  22: 9.788 ns/op
# Warmup Iteration  23: 10.737 ns/op
# Warmup Iteration  24: 11.256 ns/op
# Warmup Iteration  25: 11.163 ns/op
Iteration   1: 11.596 ns/op
Iteration   2: 9.927 ns/op
Iteration   3: 9.777 ns/op
Iteration   4: 8.570 ns/op
Iteration   5: 9.496 ns/op
Iteration   6: 9.646 ns/op
Iteration   7: 9.603 ns/op
Iteration   8: 10.324 ns/op
Iteration   9: 10.933 ns/op
Iteration  10: 10.264 ns/op
Iteration  11: 9.908 ns/op
Iteration  12: 9.765 ns/op
Iteration  13: 10.287 ns/op
Iteration  14: 10.450 ns/op
Iteration  15: 9.972 ns/op
Iteration  16: 9.248 ns/op
Iteration  17: 10.902 ns/op
Iteration  18: 11.299 ns/op
Iteration  19: 10.542 ns/op
Iteration  20: 10.069 ns/op
Iteration  21: 10.658 ns/op
Iteration  22: 10.871 ns/op
Iteration  23: 11.439 ns/op
Iteration  24: 9.626 ns/op
Iteration  25: 10.262 ns/op

Result: 10.217 ±(99.9%) 0.535 ns/op [Average]
  Statistics: (min, avg, max) = (8.570, 10.217, 11.596), stdev = 0.714
  Confidence interval (99.9%): [9.683, 10.752]


# Run complete. Total time: 00:04:08

Benchmark                     Mode   Samples        Score  Score error    Units
n.t.s.CHMTest.CHMPut          avgt        25      188.880        6.216    ns/op
n.t.s.CHMTest.CHMRemove       avgt        25       14.718        1.091    ns/op
n.t.s.CHMTest.v8CHMPut        avgt        25      150.505        4.285    ns/op
n.t.s.CHMTest.v8CHMRemove     avgt        25       10.217        0.535    ns/op
 */
public class CHMTest {
    private static final Set<Object> SET = new ConcurrentSet<>();
    private static final Set<Object> SET0 = Sets.newSetFromMap(new ConcurrentHashMap<Object, Boolean>());

    private static final Object OBJECT = new Object();

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + CHMTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(25)
                .measurementIterations(25)
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void v8CHMPut() {
        SET.add(OBJECT);
    }

    @Benchmark
    public void v8CHMRemove() {
        SET.remove(OBJECT);
    }

    @Benchmark
    public void CHMPut() {
        SET0.add(OBJECT);
    }

    @Benchmark
    public void CHMRemove() {
        SET0.remove(OBJECT);
    }
}
