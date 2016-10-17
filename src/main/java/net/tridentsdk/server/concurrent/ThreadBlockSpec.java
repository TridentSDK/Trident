package net.tridentsdk.server.concurrent;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Specifier for creating a block of threads used for
 * managing the server thread pool.
 */
@NotThreadSafe
public class ThreadBlockSpec {
    private int maxThreads;
    private boolean doStealing;
}