/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server.threads;

import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.world.World;

/**
 * World handling threads, which there are by default 4
 *
 * @author The TridentSDK Team
 */
public final class WorldThreads {
    static final ConcurrentTaskExecutor<World> THREAD_MAP = new ConcurrentTaskExecutor<>(4);

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
        return THREAD_MAP.assign(world);
    }

    /**
     * Used when the server ticks, to tell this thing to tick
     */
    protected static void notifyTick() {
        for (TaskExecutor executor : THREAD_MAP.threadList()) {
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
    }

    /**
     * Notifies the server to tick redstone activities
     */
    public static void notifyRedstoneTick() {
        for (TaskExecutor executor : THREAD_MAP.threadList()) {
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
