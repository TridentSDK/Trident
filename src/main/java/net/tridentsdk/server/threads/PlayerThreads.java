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
import java.util.*;
import java.util.concurrent.*;

/**
 * Player handling thread manager
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class PlayerThreads {
    static final Map<ThreadPlayerHandler, Integer>          THREAD_MAP  = new HashMap<>(4);
    static final Map<ClientConnection, ThreadPlayerWrapper> WRAPPER_MAP = new HashMap<>();

    static final Map<ClientConnection, ThreadPlayerWrapper> CACHE_MAP
                                                                    =
            new ConcurrentHashMap<>();
    static final ExecutorService                            SERVICE =
            Executors.newSingleThreadExecutor();

    static {
        PlayerThreads.SERVICE.execute(new Runnable() {
            @Override public void run() {
                for (int i = 0; i < 4; i++)
                    PlayerThreads.THREAD_MAP.put(new ThreadPlayerHandler(), Integer.valueOf(0));
            }
        });
    }

    private static final Map.Entry<?, ? extends Number> DEF_ENTRY = new AbstractMap.SimpleEntry<>(null, Long.MAX_VALUE);

    private PlayerThreads() {}

    /**
     * Gets the management tool for the player <p/> <p>This will put in a new value for the caches if cannot find for a
     * new player</p> <p/> <p>May block the first call</p>
     *
     * @param connection the player to find the wrapper for
     */
    public static ThreadPlayerWrapper clientThreadHandle(ClientConnection connection) {
        ThreadPlayerWrapper wrapper = PlayerThreads.CACHE_MAP.get(connection); // Fast path
        if (wrapper == null) wrapper = PlayerThreads.fallbackHandle(connection); // If not...
        return wrapper;
    }

    private static ThreadPlayerWrapper fallbackHandle(final ClientConnection connection) {
        Callable<ThreadPlayerWrapper> callable = new Callable<ThreadPlayerWrapper>() {
            @Override public ThreadPlayerWrapper call() throws Exception {
                ThreadPlayerWrapper wrapper = PlayerThreads.WRAPPER_MAP.get(connection);

                if (wrapper == null) {
                    Map.Entry<ThreadPlayerHandler, ? extends Number> handler =
                            PlayerThreads.minMap(PlayerThreads.THREAD_MAP);
                    ThreadPlayerWrapper wrap = new ThreadPlayerWrapper(handler.getKey());

                    PlayerThreads.WRAPPER_MAP.put(connection, wrap);
                    PlayerThreads.THREAD_MAP.put(handler.getKey(), Integer.valueOf(handler.getValue().intValue() + 1));

                    wrapper = wrap;
                }

                return wrapper;
            }
        };

        Future<ThreadPlayerWrapper> future = PlayerThreads.SERVICE.submit(callable);

        try {
            ThreadPlayerWrapper wrapper = future.get();
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
                ThreadPlayerWrapper wrapper = PlayerThreads.WRAPPER_MAP.remove(connection);

                if (wrapper != null) {
                    ThreadPlayerHandler handle = wrapper.getHandler();
                    PlayerThreads.THREAD_MAP.put(handle, Integer.valueOf(PlayerThreads.THREAD_MAP.get(handle) - 1));
                }
            }
        });
    }

    /**
     * Gets all of the thread player wrappers
     *
     * @return the values of the concurrent cache
     */
    public static Collection<ThreadPlayerWrapper> wrappedPlayers() {
        return PlayerThreads.CACHE_MAP.values();
    }

    private static <T> Map.Entry<T, ? extends Number> minMap(Map<T, ? extends Number> map) {
        Map.Entry<T, ? extends Number> ent = (Map.Entry<T, ? extends Number>) PlayerThreads.DEF_ENTRY;

        for (Map.Entry<T, ? extends Number> entry : map.entrySet())
            if (entry.getValue().longValue() < ent.getValue().longValue())
                ent = entry;

        return ent;
    }

    @AccessNoDoc
    static class ThreadPlayerHandler extends Thread {
        private final TransferQueue<Runnable> tasks = new LinkedTransferQueue<>();
        private boolean stopped;
        // Does not need to be volatile because only this thread can change it

        @Override
        public void run() {
            if (!this.stopped) {
                try {
                    Runnable task = this.tasks.take();
                    task.run();
                } catch (InterruptedException ignored) {
                }
                this.run();
            }
        }

        @Override public void interrupt() {
            super.interrupt();
            this.addTask(new Runnable() {
                @Override public void run() {
                    ThreadPlayerHandler.this.stopped = true;
                }
            });
        }

        public void addTask(Runnable task) {
            try {
                this.tasks.transfer(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class ThreadPlayerWrapper /* implements Player */ {
        private final ThreadPlayerHandler handler;

        /**
         * Wraps the thread player handling thread
         *
         * @param handler the handling thread to delegate actions to
         */
        ThreadPlayerWrapper(ThreadPlayerHandler handler) {
            this.handler = handler;
        }

        /**
         * Gets the thread handler for the player delegation
         *
         * @return the delegation handler
         */
        public ThreadPlayerHandler getHandler() {
            return this.handler;
        }
    }
}
