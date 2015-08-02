/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.tridentsdk.server.bench;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.tridentsdk.concurrent.SelectableThread;
import net.tridentsdk.concurrent.SelectableThreadPool;
import net.tridentsdk.event.Events;
import net.tridentsdk.event.Listener;
import net.tridentsdk.registry.Implementation;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.server.concurrent.ConcurrentTaskExecutor;
import net.tridentsdk.server.concurrent.MainThread;
import net.tridentsdk.server.concurrent.ThreadsHandler;
import net.tridentsdk.server.service.TridentImpl;
import net.tridentsdk.util.TridentLogger;
import org.apache.log4j.Level;
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
Benchmark results: http://bit.ly/1OJnXUn
 */
@State(Scope.Thread)
public class EventBusPerformance {
    static {
        TridentLogger.init(Level.ALL);
        Implementation implementation = new TridentImpl();
        Registered.setProvider(implementation);
        new MainThread(20).start();
    }

    private static final EventBus EVENT_BUS = new EventBus();
    private static final EventHandler HANDLER = new EventHandler();
    private static final Listener LISTENER = new EventListener();
    private static final Event EVENT = new Event();
    private static final SelectableThreadPool EXEC = ConcurrentTaskExecutor.create(2, "EventBusPerformance");
    private static final SelectableThread EXECUTOR = EXEC.selectScaled();
    private static final net.tridentsdk.plugin.Plugin PLUGIN = new Plugin();
    // Cannot be initialized first, else whole class cannot be loaded completely
    private static final Events EVENT_MANAGER = net.tridentsdk.server.event.EventHandler.create();
    @Param({"1", "2", "4", "8", "16", "32", "64", "128", "256"})
    private int cpuTokens;

    @Setup
    public void setup() {
        for (int i = 0; i < 50; i++) {
            EVENT_BUS.register(new EventHandler());
            EVENT_MANAGER.registerListener(PLUGIN, new EventListener());
        }
    }

    public static void main0(String[] args) {
        final EventBusPerformance performance = new EventBusPerformance();
        for (int i = 0; i < 10; i++) {
            EXEC.execute(() -> {
                // THIS IS INCORRECT - DO NOT DO IT!!!!
                EVENT_MANAGER.registerListener(PLUGIN, LISTENER);
            });
        }
        EVENT_MANAGER.fire(EVENT);
        ThreadsHandler.shutdownAll();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + EventBusPerformance.class.getSimpleName() + ".*") // CLASS
                .timeUnit(TimeUnit.NANOSECONDS).mode(Mode.AverageTime).warmupIterations(20).warmupTime(
                        TimeValue.milliseconds(1))              // ALLOWED TIME
                .measurementIterations(5).measurementTime(TimeValue.milliseconds(1))         // ALLOWED TIME
                .forks(1)                                           // FORKS
                .verbosity(VerboseMode.SILENT)                      // GRAPH
                .threads(8)                                         // THREADS
                .build();

        Benchmarks.chart(Benchmarks.parse(new Runner(opt).run()), "Event Dispatch performance (8 threads)"); // TITLE
    }

    public static void main1(String[] args) {
        EVENT_MANAGER.registerListener(PLUGIN, LISTENER);
        int i1 = 1000;
        for (int i = 0; i < i1; i++) {
            EVENT_MANAGER.fire(EVENT);
            System.out.println(EVENT.counter);
        }
    }

    public static void main2(String[] args) {
        Events events = net.tridentsdk.server.event.EventHandler.create();
        events.registerListener(PLUGIN, new EventListener());
        events.fire(new Event());
    }

    @Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void eventBusRegister() {
        //Blackhole.consumeCPU(cpuTokens);
        EVENT_BUS.register(HANDLER);
    }

    @Benchmark
    public void eventManagerRegister() {
        //Blackhole.consumeCPU(cpuTokens);
        EVENT_MANAGER.registerListener(PLUGIN, LISTENER);
    }

    @Benchmark
    public void eventBusDispatch() {
        ///Blackhole.consumeCPU(cpuTokens);
        EVENT_BUS.post(EVENT);
    }

    @Benchmark
    public void eventManagerDispatch() {
        //Blackhole.consumeCPU(cpuTokens);
        EVENT_MANAGER.fire(EVENT);
    }

    private static class Plugin extends net.tridentsdk.plugin.Plugin {
    }

    private static class Event extends net.tridentsdk.event.Event {
        int counter;
    }

    private static class EventHandler {
        @Subscribe
        public void handle(Event event) {
            if (System.nanoTime() == event.hashCode()) {
                for (int i = 0; i < 100000000; i++) {
                    System.out.println("INVALID");
                }
            }
        }
    }

    private static class EventListener implements Listener {
        //@ListenerOpts(importance = Importance.HIGHEST)
        //public void onEvent(Event event) {
        //    if (System.nanoTime() == event.hashCode()) {
        //        for (int i = 0; i < 100000000; i++) {
        //            System.out.println("INVALID");
        //        }
        //    }
        //}

        public void onEventMed(Event event) {
            event.counter++;
            if (System.nanoTime() == event.hashCode()) {
                for (int i = 0; i < 100000000; i++) {
                    System.out.println("INVALID");
                }
            }
        }

        //@ListenerOpts(importance = Importance.LOWEST)
        //public void onEventLow(Event event) {
        //    if (System.nanoTime() == event.hashCode()) {
        //        for (int i = 0; i < 100000000; i++) {
        //            System.out.println("INVALID");
        //        }
        //    }
        //}
    }
}
