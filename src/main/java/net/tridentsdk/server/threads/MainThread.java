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

import javax.annotation.concurrent.ThreadSafe;

/**
 * Handles the running of the server, the "ticks" that occur 20 times a second
 */
public class MainThread extends Thread {

    /**
     * system.currenttimemillis() when the server's first tick happened, used to keep on schedule, subject
     * to change when the server is running slow
     */
    private long zeroBase = 0;
    private volatile int ticksElapsed = 0;
    private volatile int notLostTicksElapsed = 0;

    private volatile boolean pausedTicking = false;

    private volatile int ticksToWait = 0;

    private volatile boolean redstoneTick = false;

    private final int ticksPerSecond;
    private final int tickLength;

    private static MainThread instance;

    public static MainThread getInstance() {
        return instance;
    }


    public MainThread (int ticksPerSecond) {
        zeroBase = System.currentTimeMillis();
        instance = this;
        this.ticksPerSecond = ticksPerSecond;
        tickLength = 1000/ticksPerSecond;
    }

    @Override
    public void run() {
        super.run();

        while(true) {
            if(isInterrupted()) {
                break;
            }

            long startTime = System.currentTimeMillis();

            ticksElapsed ++;

            // if we've paused, wait and then skip the rest
            if (pausedTicking) {
                calcAndWait(0);
                continue;
            }


            if(ticksToWait > 0) {
                ticksToWait--;
                calcAndWait(0);
                continue;
            }

            notLostTicksElapsed ++;


            // TODO: tick the worlds?
            WorldThreads.notifyTick();

            // alternate redstone ticks between ticks
            if(redstoneTick) {
                WorldThreads.notifyRedstoneTick();
                redstoneTick = false;
            }
            else {
                redstoneTick = true;
            }

            // TODO: decrement all timers for later tasks

            // TODO: check the worlds to make sure they're not suffering

            // TODO: run tasks that are scheduled to be run on the main thread

            calcAndWait((int) (System.currentTimeMillis() - startTime));

        }
    }

    /**
     * Interesting new feature (relative to other implementations) that would allow it to pause ticking
     */
    public boolean pauseTicking() {
        pausedTicking = true;
        return true;
    }

    /**
     * Calculates how long it should wait, and waits for that amount of time
     * @param tit the Time in Tick (tit)
     */
    private void calcAndWait(int tit) {
        correctTiming();

        int ttw = tickLength - tit;

        if(ttw <= 0) {
            return;
        }

        try {
            sleep(ttw);
        } catch (InterruptedException ex) {
            this.interrupt();
        }
    }

    private void correctTiming() {
        long expectedTime = (ticksElapsed -1) * tickLength;
        long actualTime = System.currentTimeMillis() - zeroBase;
        if(actualTime != expectedTime) {
            // if there is a difference of less than two milliseconds, just update zerobase to compensate and maintain
            // accuracy
            if(actualTime - expectedTime <= 2) {
                zeroBase += actualTime - expectedTime;
                return;
            }
            else {
                // handle more advanced divergences
            }

        }
    }

    /**
     * Instead of needing to be resumed, it will instead just skip this many ticks and resume
     * @param ticks
     * @return whether the server allows the pausing of ticking
     * @see MainThread#pauseTicking()
     */
    public boolean pauseTicking(int ticks) {

        ticksToWait += ticks;
        return true;
    }

    /**
     * Compliment to MainThread#pauseTicking(), resumes the ticking of the server
     */
    public void resumeTicking() {
        pausedTicking = false;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return super.getUncaughtExceptionHandler();
    }

    public int getTicksElapsed() {
        return ticksElapsed;
    }

    public int getNotLostTicksElapsed() {
        return notLostTicksElapsed;
    }
}
