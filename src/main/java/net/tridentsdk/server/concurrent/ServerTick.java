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
package net.tridentsdk.server.concurrent;

import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.exceptions.JiraExceptionCatcher;
import net.tridentsdk.server.player.TridentPlayer;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * This class represents the server heartbeat pulse called
 * "tick" which occurs every 1/20th of a second.
 */
public class ServerTick extends Thread {
    /**
     * The amount of time taken by a single tick
     */
    private static final long TICK_MILLIS = TimeUnit.SECONDS.toMillis(1) / 20;
    /**
     * The server which the this thread ticks
     */
    private final TridentServer server;

    /**
     * Creates a new server ticker thread.
     *
     * @param server the server to tick
     */
    public ServerTick(TridentServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            try {
                long start = System.currentTimeMillis();

                Collection<TridentPlayer> players = TridentPlayer.PLAYERS.values();
                for (TridentPlayer player : players) {
                    player.tick();
                }

                long end = System.currentTimeMillis();
                long elapsed = end - start;
                long waitTime = TICK_MILLIS - elapsed;
                if (waitTime <= 0) {
                    this.server.logger().debug("Server running behind " +
                            -waitTime + "ms, skipped " + (waitTime / TICK_MILLIS) + " ticks");
                } else {
                    Thread.sleep(waitTime);
                }
            } catch (Exception e) {
                JiraExceptionCatcher.serverException(e);
                break;
            }
        }
    }
}