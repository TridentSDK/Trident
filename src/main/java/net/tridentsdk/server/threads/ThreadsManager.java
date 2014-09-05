/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server.threads;

import net.tridentsdk.server.netty.client.ClientConnection;

import java.util.Map;

/**
 * Handles the majority of the lifecycle for the threads
 *
 * @author The TridentSDK Team
 */
public final class ThreadsManager {
    private ThreadsManager() {}

    /**
     * Stops all the executors and clears all caches of concurrent threads
     */
    public static void stopAll() {
        BackgroundTaskExecutor.SERVICE.shutdownNow();
        PlayerThreads.SERVICE.shutdownNow();

        PlayerThreads.THREAD_MAP.clear();
        PlayerThreads.WRAPPER_MAP.clear();
        for (Map.Entry<ClientConnection, PlayerThreads.ThreadPlayerWrapper> entry :
                PlayerThreads.CACHE_MAP.entrySet())
            entry.getValue().getHandler().interrupt();
        PlayerThreads.CACHE_MAP.clear();
    }
}
