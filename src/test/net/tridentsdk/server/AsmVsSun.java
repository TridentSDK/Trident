/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sun.reflect.MethodAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/*
# Run progress: 0.00% complete, ETA 00:00:50
# Warmup: 15 iterations, 1 s each
# Measurement: 10 iterations, 1 s each
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.AsmVsSun.asm
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile
.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 2.758 ns/op
# Warmup Iteration   2: 2.754 ns/op
# Warmup Iteration   3: 2.709 ns/op
# Warmup Iteration   4: 2.717 ns/op
# Warmup Iteration   5: 2.712 ns/op
# Warmup Iteration   6: 2.693 ns/op
# Warmup Iteration   7: 2.699 ns/op
# Warmup Iteration   8: 2.710 ns/op
# Warmup Iteration   9: 2.697 ns/op
# Warmup Iteration  10: 2.748 ns/op
# Warmup Iteration  11: 2.992 ns/op
# Warmup Iteration  12: 2.813 ns/op
# Warmup Iteration  13: 2.689 ns/op
# Warmup Iteration  14: 2.702 ns/op
# Warmup Iteration  15: 2.682 ns/op
Iteration   1: 2.680 ns/op
Iteration   2: 2.698 ns/op
Iteration   3: 2.694 ns/op
Iteration   4: 2.702 ns/op
Iteration   5: 2.714 ns/op
Iteration   6: 2.715 ns/op
Iteration   7: 2.724 ns/op
Iteration   8: 2.709 ns/op
Iteration   9: 2.687 ns/op
Iteration  10: 2.694 ns/op

Result: 2.702 ±(99.9%) 0.021 ns/op [Average]
  Statistics: (min, avg, max) = (2.680, 2.702, 2.724), stdev = 0.014
  Confidence interval (99.9%): [2.681, 2.722]


# Run progress: 50.00% complete, ETA 00:00:30
# Warmup: 15 iterations, 1 s each
# Measurement: 10 iterations, 1 s each
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.AsmVsSun.sun
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7534 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile
.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 2.846 ns/op
# Warmup Iteration   2: 2.852 ns/op
# Warmup Iteration   3: 7.110 ns/op
# Warmup Iteration   4: 6.570 ns/op
# Warmup Iteration   5: 4.029 ns/op
# Warmup Iteration   6: 3.976 ns/op
# Warmup Iteration   7: 3.831 ns/op
# Warmup Iteration   8: 3.882 ns/op
# Warmup Iteration   9: 3.947 ns/op
# Warmup Iteration  10: 3.963 ns/op
# Warmup Iteration  11: 3.978 ns/op
# Warmup Iteration  12: 4.139 ns/op
# Warmup Iteration  13: 4.369 ns/op
# Warmup Iteration  14: 3.903 ns/op
# Warmup Iteration  15: 3.866 ns/op
Iteration   1: 3.963 ns/op
Iteration   2: 3.969 ns/op
Iteration   3: 3.919 ns/op
Iteration   4: 3.978 ns/op
Iteration   5: 3.876 ns/op
Iteration   6: 3.981 ns/op
Iteration   7: 3.958 ns/op
Iteration   8: 3.961 ns/op
Iteration   9: 4.021 ns/op
Iteration  10: 3.979 ns/op

Result: 3.961 ±(99.9%) 0.059 ns/op [Average]
  Statistics: (min, avg, max) = (3.876, 3.961, 4.021), stdev = 0.039
  Confidence interval (99.9%): [3.902, 4.019]


# Run complete. Total time: 00:01:01

Benchmark              Mode   Samples        Score  Score error    Units
n.t.s.AsmVsSun.asm     avgt        10        2.702        0.021    ns/op
n.t.s.AsmVsSun.sun     avgt        10        3.961        0.059    ns/op
 */
public class AsmVsSun {
    private static final Obj OBJECT = new Obj();
    private static final Method METHOD = AsmVsSun.getMethod();
    private static final MethodManager<Object, Integer> METHOD_MANAGER = new MethodImpl<>(AsmVsSun.METHOD);
    private static final MethodAccess METHOD_ACCESS = MethodAccess.get(AsmVsSun.OBJECT.getClass());
    private static final int id = AsmVsSun.METHOD_ACCESS.getIndex("doStuff");

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + AsmVsSun.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS).mode(Mode.AverageTime).warmupIterations(15).measurementIterations(10)
                .forks(1).build();

        new Runner(opt).run();
    }

    private static Method getMethod() {
        try {
            return AsmVsSun.OBJECT.getClass().getDeclaredMethod("doStuff");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Benchmark
    public void asm(Blackhole blackhole) {
        blackhole.consume(AsmVsSun.METHOD_ACCESS.invoke(AsmVsSun.OBJECT, AsmVsSun.id));
    }

    @Benchmark
    public void sun(Blackhole blackhole) {
        blackhole.consume(AsmVsSun.METHOD_MANAGER.invoke(AsmVsSun.OBJECT));
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
