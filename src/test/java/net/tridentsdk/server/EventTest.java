/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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

import net.tridentsdk.Impl;
import net.tridentsdk.event.DispatchOrder;
import net.tridentsdk.event.Event;
import net.tridentsdk.event.Listener;
import net.tridentsdk.event.ListenerOpts;
import net.tridentsdk.server.command.PipelinedLogger;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.plugin.TridentEventController;
import net.tridentsdk.server.util.UncheckedCdl;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class EventTest implements Listener {
    public static void main(String[] args) throws Exception {
        Impl.setImpl(new ImplementationProvider(PipelinedLogger.init(true)));

        TridentEventController controller = TridentEventController.getInstance();
        controller.register(new EventTest());

        TestEvent event = new TestEvent();
        int its = 10;

        for (int i = 0; i < its; i++) {
            controller.dispatch(event, e -> {});
        }

        controller.dispatch(event, e -> System.out.println(e.counter));

        Options options = new OptionsBuilder()
                .include(".*" + EventTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(20)
                .measurementIterations(5)
                .forks(0)
                .threads(1)
                .build();

        new Runner(options).run();
    }

    private TestEvent event;

    @Setup(Level.Iteration)
    public void setup() {
        this.event = new TestEvent();
    }

    @TearDown
    public void tearDown() {
        ServerThreadPool.shutdownAll();
    }

    @Benchmark
    public void run() {
        UncheckedCdl cdl = new UncheckedCdl(1);
        TridentEventController.getInstance().dispatch(this.event, e -> cdl.countDown());
        cdl.await();
    }

    @ListenerOpts(order = DispatchOrder.LAST)
    public void testLastOverride(TestEvent event) {
        event.increment();
    }

    public void fireFirst(TestEvent event) {
        event.increment();
    }

    @ListenerOpts(order = DispatchOrder.LAST)
    public void fireLast(TestEvent event) {
        event.increment();
    }

    public static class TestEvent implements Event {
        private int counter;

        public void increment() {
            this.counter++;
        }
    }
}