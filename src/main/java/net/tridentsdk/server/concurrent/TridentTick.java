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
package net.tridentsdk.server.concurrent;

import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.util.JiraExceptionCatcher;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.server.world.TridentWorldLoader;

import javax.annotation.concurrent.Immutable;
import java.util.concurrent.TimeUnit;

/**
 * This class represents the server heartbeat pulse called
 * "tick" which occurs every 1/20th of a second.
 */
@Immutable
public final class TridentTick extends Thread {
    /**
     * The amount of time taken by a single tick
     */
    private static final long TICK_MILLIS = TimeUnit.SECONDS.toMillis(1) / 20;
    /**
     * The logger for this server tick thread
     */
    private final Logger logger;

    /**
     * Creates a new server ticker thread.
     */
    public TridentTick(Logger logger) {
        super("TRD - Tick");
        this.logger = logger;
    }

    @Override
    public void run() {
        while (true) {
            try {
                long start = System.currentTimeMillis();

                // Tick worlds
                for (TridentWorld world : TridentWorldLoader.getInstance().worlds()) {
                    world.tick();
                }

                // Tick players
                for (TridentPlayer player : TridentPlayer.getPlayers().values()) {
                    player.tick();
                }

                // Tick the scheduler
                TridentScheduler.getInstance().tick();

                // Timing mechanics
                long end = System.currentTimeMillis();
                long elapsed = end - start;
                long waitTime = TICK_MILLIS - elapsed;
                if (waitTime < 0) {
                    this.logger.debug("Server running behind " +
                            -waitTime + "ms, skipped " + (-waitTime / TICK_MILLIS) + " ticks");
                } else {
                    Thread.sleep(waitTime);
                }
            } catch (InterruptedException e) {
                break; // Thread interrupted by server,
                // server must be shutting down
            } catch (Exception e) {
                JiraExceptionCatcher.serverException(e);
                break;
            }
        }
    }
}