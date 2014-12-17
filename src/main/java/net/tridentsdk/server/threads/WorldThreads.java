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

import net.tridentsdk.concurrent.TaskExecutor;

import javax.annotation.concurrent.ThreadSafe;

/**
 * World handling threads, which there are by default 4
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class WorldThreads {
    private WorldThreads() {
    }

    /**
     * Used when the server ticks, to tell this thing to tick
     */
    protected static void notifyTick() {
        for (TaskExecutor executor : ThreadsManager.worldExecutor().threadList()) {
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
     * Notifies the server to tick redstone activities
     */
    public static void notifyRedstoneTick() {
        for (TaskExecutor executor : ThreadsManager.worldExecutor().threadList()) {
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
