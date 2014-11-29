package net.tridentsdk.server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.tridentsdk.api.event.Call;
import net.tridentsdk.api.event.EventManager;
import net.tridentsdk.api.event.Listenable;
import net.tridentsdk.api.event.Listener;
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
Benchmark results: http://bit.ly/1y919IB
 */
@State(Scope.Benchmark)
public class EventBusPerformance {
    private static final EventBus EVENT_BUS = new EventBus();
    private static final EventManager EVENT_MANAGER = new EventManager();

    private static final EventHandler HANDLER = new EventHandler();
    private static final Listener LISTENER = new EventListener();

    private static final Listenable EVENT = new Event();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + EventBusPerformance.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(20)
                .warmupTime(TimeValue.milliseconds(1))              // ALLOWED TIME
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(1))         // ALLOWED TIME
                .forks(1)                                           // FORKS
                .verbosity(VerboseMode.SILENT)                      // GRAPH
                .threads(1)                                         // THREADS
                .build();

        Benchmarks.chart(Benchmarks.parse(new Runner(opt).run()), "Event Dispatch performance"); // TITLE
    }

    @Param({"1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024"})
    private int cpuTokens;

    @Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void eventBusRegister() {
        Blackhole.consumeCPU(cpuTokens);
        EVENT_BUS.register(HANDLER);
    }

    @Benchmark
    public void eventManagerRegister() {
        Blackhole.consumeCPU(cpuTokens);
        EVENT_MANAGER.registerListener(LISTENER);
    }

    @Benchmark
    public void eventBusDispatch() {
        Blackhole.consumeCPU(cpuTokens);
        EVENT_BUS.post(EVENT);
    }

    @Benchmark
    public void eventManagerDispatch() {
        Blackhole.consumeCPU(cpuTokens);
        EVENT_MANAGER.call(EVENT);
    }

    private static class Event extends Listenable {
    }

    private static class EventHandler {
        @Subscribe
        public void handle(Event event) {
        }
    }

    private static class EventListener implements Listener {
        @Call
        public void onEvent(Event event) {
        }
    }
}
