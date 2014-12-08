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

import net.tridentsdk.entity.living.Player;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.threads.ThreadsManager;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class PlayerThreadTest {
    // private static final Queue<Object> QUEUE = new ConcurrentLinkedQueue<>();
    private static Player player;

    @Setup
    public static void setup() {
        for (int i = 0; i < 6_000; i++) {
            Factories.threads().playerThread(TridentPlayer.spawnPlayer(
                    ClientConnection.registerConnection(new CTXProper().channel()), UUID.randomUUID(), ""));
            // PlayerThreadTest.QUEUE.add(new CTXProper());
        }

        player = (Player) Factories.threads().players().toArray()[0];
    }

    //@Benchmark public void aRetrieval(Blackhole blackhole) {
    //    blackhole.consume(PlayerThreadTest.QUEUE.poll());
    //}

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + PlayerThreadTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(25)
                .measurementIterations(25)
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
        ThreadsManager.stopAll();
    }

    @Benchmark
    public void benchASingle() {
        //PlayerThreadTest.wrapper.doAction();
    }

    @Benchmark
    public void benchEvery() {
        //for (PlayerThreads.ThreadPlayerWrapper wrapper : PlayerThreads.players())
        //wrapper.doAction();
    }
}
