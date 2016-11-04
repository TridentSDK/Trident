package net.tridentsdk.server.concurrent;

import lombok.Getter;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Specifier for creating a block of threads used for
 * managing the server thread pool.
 */
@ThreadSafe
public class ThreadBlockSpec {
    @Getter
    private final int maxThreads;
    @Getter
    private final boolean doStealing;

    /**
     * Creates a new thread pool spec.
     *
     * @param maxThreads the max thread limit
     * @param doStealing whether or not the pool performs
     *                   work steals
     */
    public ThreadBlockSpec(int maxThreads, boolean doStealing) {
        this.maxThreads = maxThreads;
        this.doStealing = doStealing;
    }
}