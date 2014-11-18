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
package net.tridentsdk.impl.threads;

import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.world.World;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * World handling threads, which there are by default 4
 *
 * @author The TridentSDK Team
 */
public final class WorldThreads {
    static final ConcurrentTaskExecutor<World> THREAD_MAP = new ConcurrentTaskExecutor<>(4);
    static final ConcurrentCache<World, TaskExecutor> CACHE_MAP = new ConcurrentCache<>();

    static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

    private WorldThreads() {
    }

    /**
     * Gets the management tool for the world
     * <p/>
     * <p>This will put in a new value for the caches if cannot find for a new world</p>
     * <p/>
     * <p>May block the first call</p>
     *
     * @param world the world to retrieve the thread handler for
     * @return the task execution handler for the world
     */
    public static TaskExecutor worldThreadHandle(final World world) {
        return CACHE_MAP.retrieve(world, new Callable<TaskExecutor>() {
            @Override
            public TaskExecutor call() throws Exception {
                TaskExecutor executor = THREAD_MAP.getScaledThread();
                return THREAD_MAP.assign(executor, world);
            }
        }, SERVICE);
    }

    /**
     * Used when the impl ticks, to tell this thing to tick
     */
    protected static void notifyTick() {
        for (TaskExecutor executor : CACHE_MAP.values()) {
            executor.addTask(new Runnable() {
                @Override
                public void run() {
                    // TODO: maybe move this somewhere else?
                    // TODO: tick the chunk
                }
            });
        }
    }

    /**
     * Decaches the world handler from the mappings
     *
     * @param world the world to decache
     */
    public static void remove(World world) {
        THREAD_MAP.removeAssignment(world);
        CACHE_MAP.remove(world);
    }

    /**
     * Notifies the impl to tick redstone activities
     */
    public static void notifyRedstoneTick() {
        for (TaskExecutor executor : CACHE_MAP.values()) {
            executor.addTask(new Runnable() {
                @Override
                public void run() {
                    // TODO: maybe move this somewhere else?
                    // TODO: tick the world's redstone
                }
            });
        }
    }
}
