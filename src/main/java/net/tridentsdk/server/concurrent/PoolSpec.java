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

import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;

/**
 * Specifier for creating a block of threads used for
 * managing the server thread pool.
 */
@ThreadSafe
public class PoolSpec implements ThreadFactory, ForkJoinPool.ForkJoinWorkerThreadFactory {
    // Actually this is used to execute block related tick
    // methods as well such as sugar cane growing, tree leaf
    // decay, etc...
    public static final PoolSpec WORLDS = new PoolSpec("TRD - Worlds", 4, true);
    // World gen, chunk unloading and memory management
    public static final PoolSpec CHUNKS = new PoolSpec("TRD - Chunks", 4, true);

    // Self-explanatory
    public static final PoolSpec ENTITIES = new PoolSpec("TRD - Entities", 3, false);
    public static final PoolSpec PLAYERS = new PoolSpec("TRD - Players", 3, false);

    public static final PoolSpec SCHEDULER = new PoolSpec("TRD - Scheduler", 3, false);
    public static final PoolSpec PLUGINS = new PoolSpec("TRD - Plugins", 1, false);

    /**
     * The name of the pool used to identify its threads
     */
    private final String name;
    /**
     * Maximum number of parallelism that should be limited
     * in the given thread pool
     */
    @Getter
    private final int maxThreads;
    /**
     * Whether or not the task order is relevant
     */
    @Getter
    private final boolean doStealing;

    /**
     * Creates a new thread pool spec.
     *
     * @param name the name of the pool
     * @param maxThreads the max thread limit
     * @param doStealing whether or not the pool performs
     *                   work steals
     */
    public PoolSpec(String name, int maxThreads, boolean doStealing) {
        this.name = name;
        this.maxThreads = maxThreads;
        this.doStealing = doStealing;
    }

    @Override
    public Thread newThread(@Nonnull Runnable r) {
        return new Thread(r, this.name);
    }

    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
        worker.setName(this.name + " - " + worker.getPoolIndex());
        return worker;
    }
}