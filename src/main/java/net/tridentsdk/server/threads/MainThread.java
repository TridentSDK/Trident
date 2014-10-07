/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * Copyright (c) 2014, TridentSDK
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.tridentsdk.server.threads;

// TODO: probably rename this

import net.tridentsdk.api.Trident;
import net.tridentsdk.server.TridentScheduler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles the running of the server, the "ticks" that occur 20 times a second
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
        MainThread.instance = this;
        this.ticksPerSecond = ticksPerSecond;
        this.tickLength = 1000 / ticksPerSecond;
    }

    /**
     * Gets the main instance of the thread runner
     */
    public static MainThread getInstance() {
        return MainThread.instance;
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
