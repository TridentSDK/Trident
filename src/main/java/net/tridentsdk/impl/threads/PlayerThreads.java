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

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.impl.player.PlayerConnection;
import net.tridentsdk.impl.netty.ClientConnection;

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
