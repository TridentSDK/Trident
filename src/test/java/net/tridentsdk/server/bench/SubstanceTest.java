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

import net.tridentsdk.base.Substance;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Random;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
public class SubstanceTest {
    private String substance1 = "1";
    private String substance2 = "2";
    private String substance3 = "311";
    private String substance4 = "401";
    private String substance5 = "2266";
    
    private byte substance1b = 0x01;
    private byte substance3b = (byte) 311;
    private byte substance5b = (byte) 2266;

    @Param({ "0", "1", "10", "30", "40", "60", "80", "100" })
    private int cpuTokens;
    
    @Benchmark
    public void testLowSubstanceSlow (Blackhole bh) {
        Blackhole.consumeCPU(cpuTokens);
        bh.consume(Substance.fromStringId(Byte.toString(substance1b)));
    }
    
    @Benchmark
    public void testMidSubstanceSlow (Blackhole bh) {
        Blackhole.consumeCPU(cpuTokens);
        bh.consume(Substance.fromStringId(Byte.toString(substance3b)));
    }
    
    @Benchmark
    public void testHighSubstanceSlow (Blackhole bh) {
        Blackhole.consumeCPU(cpuTokens);
        bh.consume(Substance.fromStringId(Byte.toString(substance5b)));
    }
    
    @Benchmark
    public void testLowSubstanceFast (Blackhole bh) {
        Blackhole.consumeCPU(cpuTokens);
        bh.consume(Substance.fromId(substance1b));
    }

   
    @Benchmark
    public void testMidSubstanceFast (Blackhole bh) {
        Blackhole.consumeCPU(cpuTokens);
        bh.consume(Substance.fromId(substance3b));
    }

    @Benchmark
    public void testHighSubstanceFast (Blackhole bh) {
        Blackhole.consumeCPU(cpuTokens);
        bh.consume(Substance.fromId(substance5b));
    }
    
    @Benchmark
    @OperationsPerInvocation(100)
    public void testSlowSubstanceAggregate (Blackhole bh) {
        Blackhole.consumeCPU(cpuTokens);
        for(byte i = 0; i < 100; i++) {
            bh.consume(Substance.fromStringId(Byte.toString((byte)(200-i))));
        }
    }

    @Benchmark
    @OperationsPerInvocation(100)
    public void testFastSubstanceAggregate (Blackhole bh) {
        Blackhole.consumeCPU(cpuTokens);
        for(byte i = 0; i < 100; i++) {
            bh.consume(Substance.fromId((byte) (200 - i)));
        }
    }

    public static void main (String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" +SubstanceTest.class.getSimpleName() + ".*")
                .warmupIterations(3)
                .warmupTime(TimeValue.milliseconds(1))
                .measurementIterations(10)
                .measurementTime(TimeValue.milliseconds(100))
                .forks(2)
                .threads(2)
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .build();
        new Runner(opt).run();
    }
}
