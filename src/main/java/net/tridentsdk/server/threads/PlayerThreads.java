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

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.player.PlayerConnection;
import net.tridentsdk.server.netty.ClientConnection;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Player handling thread manager, 4 threads by default
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class PlayerThreads {
    static final ConcurrentTaskExecutor<Player> THREAD_MAP = new ConcurrentTaskExecutor<>(4);
    static final ConcurrentCache<Player, TaskExecutor> CACHE_MAP = new ConcurrentCache<>();

    static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

    private PlayerThreads() {
    }

    /**
     * Gets the management tool for the player
     * <p/>
     * <p>This will put in a new value for the caches if cannot find for a new player</p>
     * <p/>
     * <p>May block the first call</p>
     *
     * @param player the player to find the wrapper for
     * @return the execution tool for the player
     */
    public static TaskExecutor clientThreadHandle(final Player player) {
        return CACHE_MAP.retrieve(player, new Callable<TaskExecutor>() {
            @Override
            public TaskExecutor call() throws Exception {
                TaskExecutor executor = THREAD_MAP.getScaledThread();
                return THREAD_MAP.assign(executor, player);
            }
        }, EntityThreads.SERVICE);
    }

    /**
     * Decaches the player connection handler from the mappings
     *
     * @param connection the player to remove the wrapper cache
     */
    public static void remove(ClientConnection connection) {
        PlayerConnection pc = PlayerConnection.getConnection(connection.getAddress());
        if (pc != null) {
            Player player = pc.getPlayer();
            THREAD_MAP.removeAssignment(player);
            CACHE_MAP.remove(player);
        }
    }

    /**
     * Gets all of the thread player wrappers
     *
     * @return the values of the concurrent cache
     */
    public static Collection<Player> wrappedPlayers() {
        return THREAD_MAP.values();
    }
}
