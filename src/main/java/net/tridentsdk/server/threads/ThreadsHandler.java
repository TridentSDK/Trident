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

import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.factory.ThreadFactory;
import net.tridentsdk.server.TridentServer;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Handles the majority of the lifecycle for the threads
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class ThreadsHandler implements ThreadFactory {
    private static final ExecutorFactory entities = ConcurrentTaskExecutor.create(2, "Entities");
    private static final ExecutorFactory players = ConcurrentTaskExecutor.create(4, "Players");
    private static final ExecutorFactory worlds = ConcurrentTaskExecutor.create(2, "Worlds");

    // These 3 were originally placed together but livelock concerns have partitioned them
    private static final ExecutorFactory chunks = ConcurrentTaskExecutor.create(2, "Chunks");
    private static final ExecutorFactory generator = ConcurrentTaskExecutor.create(2, "Generator");
    private static final ExecutorFactory saver = ConcurrentTaskExecutor.create(2, "Chunk save");

    private ThreadsHandler() {
    }

    /**
     * Creates the threads handler for internal use
     *
     * @return the new thread handler
     */
    @InternalUseOnly
    public static ThreadsHandler create() {
        return new ThreadsHandler();
    }

    /**
     * Stops all the executors and clears all caches of concurrent threads
     */
    @InternalUseOnly
    public static void shutdownAll() {
        TridentServer.instance().mainThread().interrupt();
    }

    /**
     * Gets the executor for the world thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static ExecutorFactory worldExecutor() {
        return worlds;
    }

    /**
     * Gets the executor for the chunk thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static ExecutorFactory chunkExecutor() {
        return chunks;
    }

    /**
     * Gets the executor for the generator thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static ExecutorFactory genExecutor() {
        return generator;
    }

    /**
     * Gets the executor for the entity thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static ExecutorFactory entityExecutor() {
        return entities;
    }

    /**
     * Gets the executor for the player thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static ExecutorFactory playerExecutor() {
        return players;
    }

    /**
     * Gets the executor for the saving thread pool
     *
     * @return the saving thread pool
     */
    @InternalUseOnly
    public static ExecutorFactory saver() {
        return saver;
    }

    @Override
    public ExecutorFactory executor(int threads, String name) {
        return ConcurrentTaskExecutor.create(threads, name);
    }
}
