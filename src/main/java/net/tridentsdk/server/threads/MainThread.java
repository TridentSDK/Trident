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
    
    private static MainThread instance;

    public static MainThread getInstance() {
        return instance;
    }


    public MainThread () {
        zeroBase = System.currentTimeMillis();
        instance = this;
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


            // TODO: tick the worlds

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
        int ttw = 50 - tit;

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
        long expectedTime = (ticksElapsed -1)*50;
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
}
