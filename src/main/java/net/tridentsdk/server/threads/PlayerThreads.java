/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server.threads;

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.threads.TaskExecutor;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.concurrent.*;

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
     *
     * <p>This will put in a new value for the caches if cannot find for a new player</p>
     *
     * <p>May block the first call</p>
     *
     * @param player the player to find the wrapper for
     * @return the execution tool for the player
     */
    public static TaskExecutor clientThreadHandle(final Player player) {
        return PlayerThreads.CACHE_MAP.retrieve(player, new Callable<TaskExecutor>() {
            @Override
            public TaskExecutor call() throws Exception {
                TaskExecutor executor = PlayerThreads.THREAD_MAP.getScaledThread();
                PlayerThreads.THREAD_MAP.assign(executor, player);

                return executor;
            }
        }, EntityThreads.SERVICE);
    }

    /**
     * Decaches the player connection handler from the mappings
     *
     * @param player the player to remove the wrapper cache
     */
    public static void remove(Player player) {
        PlayerThreads.THREAD_MAP.removeAssignment(player);
        PlayerThreads.CACHE_MAP.remove(player);
    }

    /**
     * Gets all of the thread player wrappers
     *
     * @return the values of the concurrent cache
     */
    public static Collection<Player> wrappedPlayers() {
        return PlayerThreads.THREAD_MAP.values();
    }
}
