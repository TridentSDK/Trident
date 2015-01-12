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

import com.esotericsoftware.reflectasm.MethodAccess;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;
import sun.reflect.MethodAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/*
Benchmark charted results at: http://bit.ly/1vwutXc
 */
@State(Scope.Benchmark)
public class AsmVsSun {
    private static final Obj OBJECT = new Obj();
    private static final Method METHOD = getMethod();
    private static final MethodImpl<Object, Integer> METHOD_MANAGER = new MethodImpl<>(METHOD);
    private static final MethodAccess METHOD_ACCESS = MethodAccess.get(OBJECT.getClass());
    private static final int id = METHOD_ACCESS.getIndex("doStuff");

    @Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
    private int cpuTokens;

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + AsmVsSun.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(20)
                .warmupTime(TimeValue.milliseconds(1))
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(1))
                .forks(1)
                .verbosity(VerboseMode.SILENT)
                .build();

        Benchmarks.chart(Benchmarks.parse(new Runner(opt).run()), "Reflection+methods");
    }

    private static Method getMethod() {
        try {
            Method method = OBJECT.getClass().getDeclaredMethod("doStuff");
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public void asm(Blackhole blackhole) {
        Blackhole.consumeCPU(cpuTokens);
        blackhole.consume(METHOD_ACCESS.invoke(OBJECT, id));
    }

    @Benchmark
    public void sun(Blackhole blackhole) {
        Blackhole.consumeCPU(cpuTokens);
        blackhole.consume(METHOD_MANAGER.invoke(OBJECT));
    }

    @Benchmark
    public void normal(Blackhole blackhole) {
        Blackhole.consumeCPU(cpuTokens);
        try {
            blackhole.consume(METHOD.invoke(OBJECT));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public interface MethodManager<Declaring, T> {
        /**
         * Calls the method
         *
         * @param inst instance of the {@code class} containing the method, {@code null} for {@code static}s
         * @param args arguments the pass to the method invocation
         * @return the result of the method call
         */
        T invoke(Declaring inst, Object... args);

        /**
         * The wrapped method contained by this {@code class}
         *
         * @return the method that this {@code class} represents
         */
        Method raw();
    }

    static class Obj {
        private int anInt;

        public void doStuff() {
            this.anInt++;
        }
    }

    static class MethodImpl<D, T> implements MethodManager<D, T> {
        private final Method method;
        private final MethodAccessor accessor;

        /**
         * Wraps the Method for management by this implementation
         *
         * @param method the Method to wrap
         */
        public MethodImpl(Method method) {
            this.method = method;
            this.accessor = ReflectionFactory.getReflectionFactory().newMethodAccessor(this.method);
        }

        @Override
        public T invoke(D inst, Object... args) {
            try {
                return (T) this.accessor.invoke(inst, args);
            } catch (IllegalArgumentException | InvocationTargetException x) {
                x.printStackTrace();
            }

            return null;
        }

        @Override
        public Method raw() {
            return this.method;
        }
    }
}
