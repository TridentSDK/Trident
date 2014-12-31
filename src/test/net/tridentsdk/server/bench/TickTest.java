package net.tridentsdk.server.bench;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.config.JsonConfig;
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.ConfigFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.TridentScheduler;
import net.tridentsdk.server.threads.MainThread;
import net.tridentsdk.server.threads.ThreadsHandler;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/*
http://bit.ly/1AZxBL6

tick 5.011719043333334E7
tick 5.0066712083333336E7
tick 5.006329321666667E7
tick 5.010468563333334E7
tick 5.016053133333333E7
tick 5.012393275E7
tick 5.007866225E7
tick 5.01629668E7
tick 5.010474393333333E7
tick 5.01045108E7
tick 5.0183914333333336E7
 */
// Used for baseline measurements
@State(Scope.Benchmark) public class TickTest {
    static {
        Factories.init(new ConfigFactory() {
            @Override
            public JsonConfig serverConfig() {
                return new JsonConfig(Paths.get("/topkek"));
            }
        });
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
        Factories.init(TridentScheduler.create());
        Factories.init(ThreadsHandler.create());
    }

    private static final MainThread THREAD = new MainThread(20);
    @Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
    private int cpuTokens;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + TickTest.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS).mode(Mode.AverageTime).warmupIterations(20).warmupTime(
                        TimeValue.milliseconds(50))              // ALLOWED TIME
                .measurementIterations(5).measurementTime(TimeValue.milliseconds(50))         // ALLOWED TIME
                .forks(1)                                           // FORKS
                .verbosity(VerboseMode.SILENT)                      // GRAPH
                .threads(1)                                         // THREADS
                .build();

        Benchmarks.chart(Benchmarks.parse(new Runner(opt).run()), "Tick+Length"); // TITLE
    }

    @Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void tick() {
        THREAD.doRun();
    }
}
