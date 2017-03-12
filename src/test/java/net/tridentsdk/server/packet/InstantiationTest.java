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
package net.tridentsdk.server.packet;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@State(Scope.Benchmark)
public class InstantiationTest {
    private final ConstructorAccess<TestClass> testClassConstructorAccess = ConstructorAccess.get(TestClass.class);
    private final Supplier<TestClass> supplier = TestClass::new;

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(".*" + InstantiationTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.SECONDS)
                .mode(Mode.Throughput)
                .warmupIterations(20)
                .measurementIterations(5)
                .forks(1)
                .threads(4)
                .build();

        new Runner(options).run();
    }

    @Benchmark
    public void baseLine() {
    }

    @Benchmark
    public TestClass normalInstantiation() {
        return new TestClass();
    }

    @Benchmark
    public TestClass funcInstantiation() {
        return this.supplier.get();
    }

    @Benchmark
    public TestClass reflectionInstantiation() {
        return this.testClassConstructorAccess.newInstance();
    }

    public static class TestClass {
    }
}