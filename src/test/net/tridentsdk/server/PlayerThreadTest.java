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

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.threads.PlayerThreads;
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
            PlayerThreads.clientThreadHandle(TridentPlayer.spawnPlayer(
                    ClientConnection.registerConnection(new CTXProper().channel()), UUID.randomUUID(), ""));
            // PlayerThreadTest.QUEUE.add(new CTXProper());
        }

        player = (Player) PlayerThreads.wrappedPlayers().toArray()[0];
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
        //for (PlayerThreads.ThreadPlayerWrapper wrapper : PlayerThreads.wrappedPlayers())
        //wrapper.doAction();
    }
}
