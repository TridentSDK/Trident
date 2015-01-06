package net.tridentsdk.server.bench;

import net.tridentsdk.concurrent.HeldValueLatch;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.concurrent.TimeUnit;

/*
Benchmark results: http://bit.ly/1rdMWrp
 */
@State(Scope.Benchmark) public class LatchTest {
    private static final HeldValueLatch<HeldValueLatch<?>> LATCH = HeldValueLatch.create();
    @Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
    private int cpuTokens;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + LatchTest.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS).mode(Mode.AverageTime).warmupIterations(20).warmupTime(
                        TimeValue.milliseconds(1))              // ALLOWED TIME
                .measurementIterations(5).measurementTime(TimeValue.milliseconds(1))         // ALLOWED TIME
                .forks(1)                                           // FORKS
                .verbosity(VerboseMode.SILENT)                      // GRAPH
                .threads(1)                                         // THREADS
                .build();

        Benchmarks.chart(Benchmarks.parse(new Runner(opt).run()), "Latch+Benchmark"); // TITLE
    }

    @Setup
    public void setup() {
        LATCH.countDown(LATCH);
    }

    @Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void down() {
        Blackhole.consumeCPU(cpuTokens);
        LATCH.countDown(LATCH);
    }

    @Benchmark
    public void wait(Blackhole blackhole) {
        Blackhole.consumeCPU(cpuTokens);
        try {
            blackhole.consume(LATCH.await());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
