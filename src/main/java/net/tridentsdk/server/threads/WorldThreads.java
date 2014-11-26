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

/**
 * World handling threads, which there are by default 4
 *
 * @author The TridentSDK Team
 */
public final class WorldThreads {
    private WorldThreads() {
    }

    /**
     * Used when the server ticks, to tell this thing to tick
     */
    protected static void notifyTick() {
        for (TaskExecutor executor : ThreadsManager.worlds.threadList()) {
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
        for (TaskExecutor executor : ThreadsManager.worlds.threadList()) {
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
