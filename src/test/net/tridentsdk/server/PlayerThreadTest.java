/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server;

import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.threads.PlayerThreads;
import net.tridentsdk.server.threads.ThreadsManager;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class PlayerThreadTest {
    // private static final Queue<Object> QUEUE = new ConcurrentLinkedQueue<>();

    @Setup public static void setup() {
        for (int i = 0; i < 6_000; i++) {
            PlayerThreads.clientThreadHandle(ClientConnection.registerConnection(new CTXProper()));
            // PlayerThreadTest.QUEUE.add(new CTXProper());
        }
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

    @Benchmark public void benchASingle() {
        ((PlayerThreads.ThreadPlayerWrapper) PlayerThreads.wrappedPlayers().toArray()[0]).doAction();
    }

    @Benchmark public void benchEvery() {
        for (PlayerThreads.ThreadPlayerWrapper wrapper : PlayerThreads.wrappedPlayers())
            wrapper.doAction();
    }
}
