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

package net.tridentsdk.server.threads;

import net.tridentsdk.entity.living.Player;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.TridentScheduler;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles the running of the server, the "ticks" that occur 20 times a second
 *
 * @author The TridentSDK Team
 */
@ThreadSafe public class MainThread extends Thread {
    private static MainThread instance;
    private final AtomicInteger ticksElapsed = new AtomicInteger();
    private final AtomicInteger notLostTicksElapsed = new AtomicInteger();
    private final AtomicInteger ticksToWait = new AtomicInteger();
    private final int ticksPerSecond;
    private final int tickLength;

    /**
     * system.currenttimemillis() when the server's first tick happened, used to keep on schedule, subject to change
     * when the server is running slow
     */
    private long zeroBase;
    private volatile boolean pausedTicking;
    private volatile boolean redstoneTick;

    /**
     * Creates the MainThread runner from the amount of heartbeats the server should take per second the server runs
     *
     * @param ticksPerSecond the amount of heartbeats per second
     */
    public MainThread(int ticksPerSecond) {
        this.zeroBase = System.currentTimeMillis();
        instance = this;
        this.ticksPerSecond = ticksPerSecond;
        this.tickLength = 1000 / ticksPerSecond;
    }

    /**
     * Gets the main instance of the thread runner
     */
    public static MainThread getInstance() {
        return instance;
    }

    public void doRun() {
        long startTime = System.currentTimeMillis();

        this.ticksElapsed.getAndIncrement();

        // if we've paused, wait and then skip the rest
        if (this.pausedTicking) {
            this.calcAndWait(0);
            return;
        }

        if (this.ticksToWait.get() > 0) {
            this.ticksToWait.getAndDecrement();
            this.calcAndWait(0);
            return;
        }

        this.notLostTicksElapsed.getAndIncrement();

        // TODO: tick the worlds?
        WorldThreads.notifyTick();

        /**
         * Tick the players
         */
        for (final Player player : TridentPlayer.getPlayers()) {
            player.tick();
        }

        // alternate redstone ticks between ticks
        if (this.redstoneTick) {
            WorldThreads.notifyRedstoneTick();
            this.redstoneTick = false;
        } else {
            this.redstoneTick = true;
        }

        // TODO: check the worlds to make sure they're not suffering

        ((TridentScheduler) Factories.tasks()).tick();

        this.calcAndWait((int) (System.currentTimeMillis() - startTime));
    }

    @Override
    public void run() {
        super.run();

        while (!this.isInterrupted()) {
            doRun();
        }
    }

    /**
     * Interesting new feature (relative to other implementations) that would allow it to pause ticking
     */
    public boolean pauseTicking() {
        // TODO: configurable
        this.pausedTicking = true;
        return true;
    }

    /**
     * Calculates how long it should wait, and waits for that amount of time
     *
     * @param tit the Time in Tick (tit)
     */
    private void calcAndWait(int tit) {
        this.correctTiming();

        int ttw = this.tickLength - tit;

        if (ttw <= 0) {
            return;
        }

        try {
            Thread.sleep((long) ttw);
        } catch (InterruptedException ex) {
            this.interrupt();
        }
    }

    private void correctTiming() {
        long expectedTime = (long) ((this.ticksElapsed.get() - 1) * this.tickLength);
        long actualTime = System.currentTimeMillis() - this.zeroBase;
        if (actualTime != expectedTime) {
            // if there is a difference of less than two milliseconds, just update zerobase to compensate and maintain
            // accuracy
            if (actualTime - expectedTime <= 2L) {
                this.zeroBase += actualTime - expectedTime;
            }

            // handle more advanced divergences
        }
    }

    /**
     * Instead of needing to be resumed, it will instead just skip this many ticks and resume
     *
     * @param ticks the ticks to pause for
     * @return whether the server allows the pausing of ticking
     * @see MainThread#pauseTicking()
     */
    public boolean pauseTicking(int ticks) {
        this.ticksToWait.addAndGet(ticks);
        return true;
    }

    /**
     * Compliment to MainThread#pauseTicking(), resumes the ticking of the server
     */
    public void resumeTicking() {
        this.pausedTicking = false;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return super.getUncaughtExceptionHandler();
    }

    /**
     * Gets the elapsed ticks
     *
     * @return the ticks elapsed
     */
    public int getTicksElapsed() {
        return this.ticksElapsed.get();
    }

    /**
     * Gets the ticks that were not lost in the time elapsed
     *
     * @return the ticks not lost
     */
    public int getNotLostTicksElapsed() {
        return this.notLostTicksElapsed.get();
    }
}
