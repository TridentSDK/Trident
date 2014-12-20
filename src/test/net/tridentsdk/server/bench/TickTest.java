package net.tridentsdk.server.bench;

import net.tridentsdk.server.threads.MainThread;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

// Used for baseline measurements
public class TickTest {
    private static final MainThread THREAD = new MainThread(20);

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + TickTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(20)
                .warmupTime(TimeValue.milliseconds(60))
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(60))
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void tick() {
        THREAD.doRun();
    }
}
