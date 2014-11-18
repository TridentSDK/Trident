/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.impl.threads;

import net.tridentsdk.api.Trident;
import net.tridentsdk.impl.TridentScheduler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles the running of the impl, the "ticks" that occur 20 times a second
 *
 * @author The TridentSDK Team
 */
public class MainThread extends Thread {
    private static MainThread instance;
    private final AtomicInteger ticksElapsed = new AtomicInteger();
    private final AtomicInteger notLostTicksElapsed = new AtomicInteger();
    private final AtomicInteger ticksToWait = new AtomicInteger();
    private final int ticksPerSecond;
    private final int tickLength;

    /**
     * system.currenttimemillis() when the impl's first tick happened, used to keep on schedule, subject to change
     * when the impl is running slow
     */
    private long zeroBase;
    private volatile boolean pausedTicking;
    private volatile boolean redstoneTick;

    /**
     * Creates the MainThread runner from the amount of heartbeats the impl should take per second the impl runs
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

    @Override
    public void run() {
        super.run();

        while (true) {
            if (this.isInterrupted()) {
                break;
            }

            long startTime = System.currentTimeMillis();

            this.ticksElapsed.getAndIncrement();

            // if we've paused, wait and then skip the rest
            if (this.pausedTicking) {
                this.calcAndWait(0);
                continue;
            }

            if (this.ticksToWait.get() > 0) {
                this.ticksToWait.getAndDecrement();
                this.calcAndWait(0);
                continue;
            }

            this.notLostTicksElapsed.getAndIncrement();

            // TODO: tick the worlds?
            WorldThreads.notifyTick();

            // alternate redstone ticks between ticks
            if (this.redstoneTick) {
                WorldThreads.notifyRedstoneTick();
                this.redstoneTick = false;
            } else {
                this.redstoneTick = true;
            }

            // TODO: check the worlds to make sure they're not suffering

            ((TridentScheduler) Trident.getServer().getScheduler()).tick();

            this.calcAndWait((int) (System.currentTimeMillis() - startTime));
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
     * @return whether the impl allows the pausing of ticking
     * @see net.tridentsdk.impl.threads.MainThread#pauseTicking()
     */
    public boolean pauseTicking(int ticks) {
        this.ticksToWait.addAndGet(ticks);
        return true;
    }

    /**
     * Compliment to MainThread#pauseTicking(), resumes the ticking of the impl
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
