/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server.threads;

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
    static final ConcurrentCache<World, ConcurrentTaskExecutor.TaskExecutor> CACHE_MAP = new ConcurrentCache<>();

    static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

    private WorldThreads() {
    }

    /**
     * Gets the management tool for the world <p/> <p>This will put in a new value for the caches if cannot find for a
     * new world</p> <p/> <p>May block the first call</p>
     *
     * @param world the world to retrieve the thread handler for
     * @return the task execution handler for the world
     */
    public static ConcurrentTaskExecutor.TaskExecutor worldThreadHandle(final World world) {
        return WorldThreads.CACHE_MAP.retrieve(world, new Callable<ConcurrentTaskExecutor.TaskExecutor>() {
            @Override
            public ConcurrentTaskExecutor.TaskExecutor call() throws Exception {
                ConcurrentTaskExecutor.TaskExecutor executor = WorldThreads.THREAD_MAP.getScaledThread();
                WorldThreads.THREAD_MAP.assign(executor, world);

                return executor;
            }
        }, WorldThreads.SERVICE);
    }

    /**
     * Used when the server ticks, to tell this thing to tick
     */
    protected static void notifyTick() {
        for (ConcurrentTaskExecutor.TaskExecutor executor : WorldThreads.CACHE_MAP.values()) {
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
        WorldThreads.THREAD_MAP.removeAssignment(world);
        WorldThreads.CACHE_MAP.remove(world);
    }

    public static void notifyRedstoneTick() {
        for (ConcurrentTaskExecutor.TaskExecutor executor : WorldThreads.CACHE_MAP.values()) {
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
