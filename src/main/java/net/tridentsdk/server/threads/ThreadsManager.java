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

import net.tridentsdk.api.docs.InternalUseOnly;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.factory.ExecutorFactory;
import net.tridentsdk.api.factory.ThreadFactory;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.world.World;
import net.tridentsdk.player.PlayerConnection;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.server.netty.ClientConnection;

import java.util.Collection;

/**
 * Handles the majority of the lifecycle for the threads
 *
 * @author The TridentSDK Team
 */
public final class ThreadsManager implements ThreadFactory {
    static final ExecutorFactory<Entity> entities = new ConcurrentTaskExecutor<>(2);
    static final ExecutorFactory<Player> players = new ConcurrentTaskExecutor<>(4);
    static final ExecutorFactory<TridentPlugin> plugins = new ConcurrentTaskExecutor<>(2);
    static final ExecutorFactory<World> worlds = new ConcurrentTaskExecutor<>(4);

    /**
     * Stops all the executors and clears all caches of concurrent threads
     */
    @InternalUseOnly
    public static void stopAll() {
        BackgroundTaskExecutor.SERVICE.shutdownNow();
        MainThread.getInstance().interrupt();

        // TODO safely add hooks
        entities.shutdown();
        players.shutdown();
        plugins.shutdown();
        worlds.shutdown();
    }

    /**
     * Decaches the entity handler from the mappings
     *
     * @param entity the entity to decache
     */
    @InternalUseOnly
    public static void remove(Entity entity) {
        entities.removeAssignment(entity);
    }

    /**
     * Decaches the player connection handler from the mappings
     *
     * @param connection the player to remove the wrapper cache
     */
    @InternalUseOnly
    public static void remove(ClientConnection connection) {
        PlayerConnection pc = PlayerConnection.getConnection(connection.getAddress());
        if (pc != null) {
            Player player = pc.getPlayer();
            players.removeAssignment(player);
        }
    }

    /**
     * Decaches the plugin handler from the mappings
     *
     * @param plugin the plugin to remove from the cache
     */
    @InternalUseOnly
    public static void remove(TridentPlugin plugin) {
        plugins.removeAssignment(plugin);
    }

    /**
     * Decaches the world handler from the mappings
     *
     * @param world the world to decache
     */
    @InternalUseOnly
    public static void remove(World world) {
        worlds.removeAssignment(world);
    }

    @Override
    public TaskExecutor entityThread(Entity entity) {
        return entities.assign(entity);
    }

    @Override
    public Collection<Entity> entities() {
        return entities.values();
    }

    @Override
    public TaskExecutor playerThread(Player player) {
        return players.assign(player);
    }

    @Override
    public Collection<Player> players() {
        return players.values();
    }

    @Override
    public TaskExecutor worldThread(World world) {
        return worlds.assign(world);
    }

    @Override
    public Collection<World> worlds() {
        return worlds.values();
    }

    @Override
    public <T> ExecutorFactory<T> executor(int threads) {
        return new ConcurrentTaskExecutor<>(threads);
    }
}
