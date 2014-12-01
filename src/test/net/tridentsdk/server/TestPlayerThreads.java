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

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.factory.Factories;
import net.tridentsdk.player.TridentPlayer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.threads.ThreadsManager;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
# Run progress: 0.00% complete, ETA 00:00:30
# Warmup: 10 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 10 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.TestPlayerThreads.autoBox
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7537 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile
.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 7.162 ns/op
# Warmup Iteration   2: 7.341 ns/op
# Warmup Iteration   3: 7.063 ns/op
# Warmup Iteration   4: 7.082 ns/op
# Warmup Iteration   5: 7.098 ns/op
# Warmup Iteration   6: 7.140 ns/op
# Warmup Iteration   7: 7.110 ns/op
# Warmup Iteration   8: 7.044 ns/op
# Warmup Iteration   9: 7.182 ns/op
# Warmup Iteration  10: 7.129 ns/op
Iteration   1: 6.986 ns/op
Iteration   2: 7.131 ns/op
Iteration   3: 7.120 ns/op
Iteration   4: 7.107 ns/op
Iteration   5: 7.221 ns/op

Result: 7.113 �(99.9%) 0.323 ns/op [Average]
  Statistics: (min, avg, max) = (6.986, 7.113, 7.221), stdev = 0.084
  Confidence interval (99.9%): [6.790, 7.436]


# Run progress: 50.00% complete, ETA 00:00:18
# Warmup: 10 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 10 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.TestPlayerThreads.explicitBox
# VM invoker: /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
# VM options: -Didea.launcher.port=7537 -Didea.launcher.bin.path=/media/A4F1-7AB7/idea-IU-135.1230/bin -Dfile
.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 7.340 ns/op
# Warmup Iteration   2: 7.310 ns/op
# Warmup Iteration   3: 7.067 ns/op
# Warmup Iteration   4: 7.191 ns/op
# Warmup Iteration   5: 7.152 ns/op
# Warmup Iteration   6: 7.125 ns/op
# Warmup Iteration   7: 7.173 ns/op
# Warmup Iteration   8: 7.327 ns/op
# Warmup Iteration   9: 7.021 ns/op
# Warmup Iteration  10: 7.139 ns/op
Iteration   1: 7.196 ns/op
Iteration   2: 7.005 ns/op
Iteration   3: 7.087 ns/op
Iteration   4: 7.153 ns/op
Iteration   5: 7.044 ns/op

Result: 7.097 �(99.9%) 0.301 ns/op [Average]
  Statistics: (min, avg, max) = (7.005, 7.097, 7.196), stdev = 0.078
  Confidence interval (99.9%): [6.796, 7.398]


# Run complete. Total time: 00:00:36

Benchmark                               Mode   Samples        Score  Score error    Units
n.t.s.TestPlayerThreads.autoBox         avgt         5        7.113        0.323    ns/op
n.t.s.TestPlayerThreads.explicitBox     avgt         5        7.097        0.301    ns/op
 */
public class TestPlayerThreads {
    public static final Player PLAYER = TridentPlayer.spawnPlayer(ClientConnection.registerConnection(
            new CTXProper().channel()), UUID.randomUUID(), "");

    /* @Benchmark public void explicitBox(Blackhole blackhole) {
        blackhole.consume(Integer.valueOf(69));
    }

    @Benchmark public void autoBox(Blackhole blackhole) {
        blackhole.consume((Object) 69);
    } */

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + TestPlayerThreads.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(10)
                .measurementIterations(5)
                .forks(1)
                .threads(10)
                .build();

        new Runner(opt).run();
        ThreadsManager.stopAll();
    }

    @Benchmark
    public void put(Blackhole blackhole) {
        blackhole.consume(Factories.threads().playerThread(PLAYER));
    }

    @Benchmark
    public void remove(Blackhole blackhole) {
        //PlayerThreads.remove(TestPlayerThreads.PLAYER);
    }
}
