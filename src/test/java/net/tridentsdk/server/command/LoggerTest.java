/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.command;

import net.tridentsdk.command.logger.Logger;
import org.fusesource.jansi.AnsiConsole;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class LoggerTest {
    public static void main(String[] args) throws RunnerException, InterruptedException {
        run();
        // run1();
    }

    public static void run1() throws InterruptedException {
        LoggerTest test = new LoggerTest();
        Executor e = Executors.newFixedThreadPool(1);
        while (true) {
            // Introduce breakpoints for profilers
            Thread.sleep(10);
            if (ThreadLocalRandom.current().nextInt(50) == 1) {
                test.setup();
            }

            e.execute(test::log);
        }
    }

    public static void run() throws RunnerException {
        Options options = new OptionsBuilder()
                .include(".*" + LoggerTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.SECONDS)
                .mode(Mode.Throughput)
                .warmupIterations(20)
                .measurementIterations(5)
                .forks(1)
                .threads(4)
                .build();

        new Runner(options).run();
    }

    private final Logger logger;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public LoggerTest() {
        AnsiConsole.systemInstall();
        System.setOut(new PrintStream(out));
        System.setErr(System.out);

        PipelinedLogger underlying = new DefaultLogger();
        PipelinedLogger colorizer = new ColorizerLogger(underlying);
        PipelinedLogger debugger = DebugLogger.noop(colorizer);
        PipelinedLogger handler = new LoggerHandlers(debugger);
        PipelinedLogger file = null;
        try {
            file = FileLogger.init(handler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger = InfoLogger.get(file, "Bench");
    }

    private volatile String print;

    @Setup(Level.Iteration)
    public void setup() {
        byte[] barray = new byte[64];
        ThreadLocalRandom.current().nextBytes(barray);
        print = new String(barray);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        out.reset();
    }

    @Benchmark
    public void log() {
        logger.log(print);
    }
}