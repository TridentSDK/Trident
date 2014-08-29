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

import net.tridentsdk.api.docs.AccessNoDoc;
import net.tridentsdk.server.netty.client.ClientConnection;

import javax.annotation.concurrent.ThreadSafe;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Player handling thread manager
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class PlayerThreads {
    static final Map<PlayerThreads.ThreadPlayerHandler, Integer>                        THREAD_MAP  =
            new HashMap<>(16);
    static final Map<ClientConnection, PlayerThreads.ThreadPlayerWrapper>               WRAPPER_MAP =
            new HashMap<>();
    static final ConcurrentHashMap<ClientConnection, PlayerThreads.ThreadPlayerWrapper> CACHE_MAP   =
            new ConcurrentHashMap<>();

    static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

    static {
        PlayerThreads.SERVICE.execute(new Runnable() {
            @Override public void run() {
                for (int i = 0; i < 16; i++)
                    PlayerThreads.THREAD_MAP.put(new PlayerThreads.ThreadPlayerHandler(), Integer.valueOf(0));
            }
        });
    }

    private static final Map.Entry<?, ? extends Number> DEF_ENTRY = new AbstractMap.SimpleEntry<>(null, Long.MAX_VALUE);

    private PlayerThreads() {}

    /**
     * Gets the management tool for the player
     * <p/>
     * <p>This will put in a new value for the caches if cannot find for a new player</p>
     * <p/>
     * <p>May block the first call</p>
     *
     * @param connection the player to find the wrapper for
     */
    public static PlayerThreads.ThreadPlayerWrapper clientThreadHandle(ClientConnection connection) {
        PlayerThreads.ThreadPlayerWrapper wrapper = PlayerThreads.CACHE_MAP.get(connection); // Fast path
        if (wrapper == null) wrapper = PlayerThreads.fallbackHandle(connection); // If not...
        return wrapper;
    }

    private static PlayerThreads.ThreadPlayerWrapper fallbackHandle(final ClientConnection connection) {
        Callable<PlayerThreads.ThreadPlayerWrapper> callable = new Callable<PlayerThreads.ThreadPlayerWrapper>() {
            @Override public PlayerThreads.ThreadPlayerWrapper call() throws Exception {
                PlayerThreads.ThreadPlayerWrapper wrapper = PlayerThreads.WRAPPER_MAP.get(connection);
                if (wrapper == null) {
                    Map.Entry<PlayerThreads.ThreadPlayerHandler, ? extends Number> handler =
                            PlayerThreads.minMap(PlayerThreads.THREAD_MAP);
                    PlayerThreads.ThreadPlayerWrapper wrap = new PlayerThreads.ThreadPlayerWrapper(handler.getKey());

                    PlayerThreads.WRAPPER_MAP.put(connection, wrap);
                    PlayerThreads.THREAD_MAP.put(handler.getKey(), Integer.valueOf(handler.getValue().intValue() + 1));

                    wrapper = wrap;
                }

                return wrapper;
            }
        };

        Future<PlayerThreads.ThreadPlayerWrapper> future = PlayerThreads.SERVICE.submit(callable);
        try {
            PlayerThreads.ThreadPlayerWrapper wrapper = future.get();
            if (wrapper != null) PlayerThreads.CACHE_MAP.put(connection, wrapper);
            return wrapper;
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }

    /**
     * Decaches the player connection handler from the mappings
     *
     * @param connection the player to remove the wrapper cache
     */
    public static void remove(final ClientConnection connection) {
        PlayerThreads.SERVICE.execute(new Runnable() {
            @Override public void run() {
                PlayerThreads.ThreadPlayerWrapper wrapper = PlayerThreads.WRAPPER_MAP.remove(connection);
                if (wrapper != null) {
                    PlayerThreads.ThreadPlayerHandler handle = wrapper.getHandler();
                    PlayerThreads.THREAD_MAP.put(handle, Integer.valueOf(PlayerThreads.THREAD_MAP.get(handle) - 1));
                }
            }
        });
    }

    private static <T> Map.Entry<T, ? extends Number> minMap(Map<T, ? extends Number> map) {
        Map.Entry<T, ? extends Number> ent = (Map.Entry<T, ? extends Number>) PlayerThreads.DEF_ENTRY;
        for (Map.Entry<T, ? extends Number> entry : map.entrySet())
            if (entry.getValue().longValue() < ent.getValue().longValue())
                ent = entry;
        return ent;
    }

    // TODO put player implementation here, or move somewhere?
    @AccessNoDoc
    static class ThreadPlayerHandler extends Thread {
        private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

        @Override public void run() {
            while (!this.isInterrupted()) {
                try {
                    Runnable task = tasks.poll();
                    if (task != null)
                        task.run();
                } catch (Exception x) {
                    continue; // Keep going :)
                }
            }
        }
    }

    public static class ThreadPlayerWrapper /* implements Player */ {
        private final PlayerThreads.ThreadPlayerHandler handler;

        /**
         * Wraps the thread player handling thread
         *
         * @param handler the handling thread to delegate actions to
         */
        ThreadPlayerWrapper(PlayerThreads.ThreadPlayerHandler handler) {
            this.handler = handler;
        }

        /**
         * Gets the thread handler for the player delegation
         *
         * @return the delegation handler
         */
        public PlayerThreads.ThreadPlayerHandler getHandler() {
            return this.handler;
        }
    }
}
