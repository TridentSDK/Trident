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

package net.tridentsdk.server.concurrent;

import net.tridentsdk.Trident;
import net.tridentsdk.concurrent.SelectableThreadPool;
import net.tridentsdk.config.Config;
import net.tridentsdk.config.ConfigSection;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.util.TridentLogger;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Handles the majority of the lifecycle for the concurrent
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class ThreadsHandler {
    private static final Config cfg = new Config(Trident.fileContainer().resolve("server.json")); // Initialization code can't use factory
    private static final ConfigSection section = cfg.getConfigSection("performance");

    private static final SelectableThreadPool entities = configure("Entities");
    // private static final ExecutorFactory entities = configure("Tile-Entities"); not needed yet
    private static final SelectableThreadPool players = configure("Players");
    private static final SelectableThreadPool worlds = configure("Worlds");

    // These 2 were originally placed together but livelock concerns have partitioned them
    private static final SelectableThreadPool chunks = configure("Chunks");
    private static final SelectableThreadPool generator = configure("Generator");

    private ThreadsHandler() {
    }

    public static SelectableThreadPool configure(String uppercaseName) {
        String tagName = uppercaseName.toLowerCase() + "-threads";
        int threads = section.getInt(tagName);
        if (threads == 0) {
            threads = 2;
            TridentLogger.warn("Could not find config field for " + tagName + ", using 2 threads instead.");
        }

        return ConcurrentTaskExecutor.create(threads, uppercaseName);
    }

    /**
     * Creates the concurrent handler for internal use
     *
     * @return the new thread handler
     */
    @InternalUseOnly
    public static ThreadsHandler create() {
        return new ThreadsHandler();
    }

    /**
     * Stops all the executors and clears all caches of concurrent concurrent
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
    public static SelectableThreadPool worldExecutor() {
        return worlds;
    }

    /**
     * Gets the executor for the chunk thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static SelectableThreadPool chunkExecutor() {
        return chunks;
    }

    /**
     * Gets the executor for the generator thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static SelectableThreadPool genExecutor() {
        return generator;
    }

    /**
     * Gets the executor for the entity thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static SelectableThreadPool entityExecutor() {
        return entities;
    }

    /**
     * Gets the executor for the player thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static SelectableThreadPool playerExecutor() {
        return players;
    }
}
