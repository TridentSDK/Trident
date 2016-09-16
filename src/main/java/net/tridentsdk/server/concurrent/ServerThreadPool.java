package net.tridentsdk.server.concurrent;

/**
 * Managed set of threads that can be constrained
 * in CPU resources and performs work stealing when
 * necessary.
 */
public class ServerThreadPool {
    private static final int DOUBLING_THRESH = 8;

    private final Worker[] workers;

    public ServerThreadPool() {
        int cpus = Runtime.getRuntime().availableProcessors();
        if (cpus < DOUBLING_THRESH) {
            cpus <<= 1;

        }

        this.workers = new Worker[cpus];
    }

    private class Worker extends Thread {
        public Worker(int idx) {
            super("TRD - Worker - " + idx);
        }
    }
}