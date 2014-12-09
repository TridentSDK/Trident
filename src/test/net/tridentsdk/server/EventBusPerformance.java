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

package net.tridentsdk.server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.concurrent.TaskExecutor;
import net.tridentsdk.config.JsonConfig;
import net.tridentsdk.event.Listener;
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.ConfigFactory;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.threads.ThreadsManager;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.File;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/*
Benchmark results: http://bit.ly/1B3psZv
 */
@State(Scope.Thread)
public class EventBusPerformance {
    private static final EventBus EVENT_BUS = new EventBus();

    // Cannot be initialized first, else whole class cannot be loaded completely
    private final net.tridentsdk.event.EventHandler EVENT_MANAGER = new net.tridentsdk.event.EventHandler();

    static {
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
        Factories.init(new ThreadsManager());
        Factories.init(new TridentScheduler());

        final JsonConfig innerConfig = new JsonConfig(new File("toplel"));
        Factories.init(new ConfigFactory() {
            @Override
            public JsonConfig serverConfig() {
                return innerConfig;
            }
        });
    }

    private static final EventHandler HANDLER = new EventHandler();
    private static final Listener LISTENER = new EventListener();

    private static final net.tridentsdk.event.Event EVENT = new Event();

    private static final ExecutorFactory<?> EXEC = Factories
            .threads()
            .executor(2);

    private static final TaskExecutor EXECUTOR = EXEC.scaledThread();

    public void main1(String[] args) {
        while (true) {
            EVENT_MANAGER.registerListener(EXECUTOR, LISTENER);
        }
    }

    public static void main0(String[] args) {
        final EventBusPerformance performance = new EventBusPerformance();
        for (int i = 0; i < 10; i++) {
            EXEC.scaledThread().addTask(new Runnable() {
                @Override
                public void run() {
                    // THIS IS INCORRECT - DO NOT DO IT!!!!
                    performance.EVENT_MANAGER.registerListener(EXEC.scaledThread(), LISTENER);
                }
            });
        }
        performance.EVENT_MANAGER.call(EVENT);
        ThreadsManager.stopAll();
    }

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
                .verbosity(VerboseMode.NORMAL)                      // GRAPH
                .threads(4)                                         // THREADS
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
        EVENT_MANAGER.registerListener(EXECUTOR, LISTENER);
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

    private static class Event extends net.tridentsdk.event.Event {
    }

    private static class EventHandler {
        @Subscribe
        public void handle(Event event) {
        }
    }

    private static class EventListener implements Listener {
        public void onEvent(Event event) {
            System.out.println("LOL");
        }
    }
}
