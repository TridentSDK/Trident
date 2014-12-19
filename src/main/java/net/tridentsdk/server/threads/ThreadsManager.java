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
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.factory.ThreadFactory;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.world.World;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;

/**
 * Handles the majority of the lifecycle for the threads
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class ThreadsManager implements ThreadFactory {
    private static final ExecutorFactory<Entity> entities = new ConcurrentTaskExecutor<>(2);
    private static final ExecutorFactory<Player> players = new ConcurrentTaskExecutor<>(4);
    private static final ExecutorFactory<TridentPlugin> plugins = new ConcurrentTaskExecutor<>(2);
    private static final ExecutorFactory<World> worlds = new ConcurrentTaskExecutor<>(4);

    /**
     * Stops all the executors and clears all caches of concurrent threads
     */
    @InternalUseOnly
    public static void stopAll() {
        BackgroundTaskExecutor.SERVICE.shutdownNow();
        MainThread.getInstance().interrupt();

        // TODO safely add hooks
        entityExecutor().shutdown();
        playerExecutor().shutdown();
        pluginExecutor().shutdown();
        worldExecutor().shutdown();
    }

    /**
     * Decaches the entity handler from the mappings
     *
     * @param entity the entity to decache
     */
    @InternalUseOnly
    public static void remove(Entity entity) {
        entityExecutor().removeAssignment(entity);
    }

    /**
     * Decaches the player connection handler from the mappings
     *
     * @param player the player to remove the wrapper cache
     */
    @InternalUseOnly
    public static void remove(Player player) {
        playerExecutor().removeAssignment(player);
    }

    /**
     * Decaches the plugin handler from the mappings
     *
     * @param plugin the plugin to remove from the cache
     */
    @InternalUseOnly
    public static void remove(TridentPlugin plugin) {
        pluginExecutor().removeAssignment(plugin);
    }

    /**
     * Decaches the world handler from the mappings
     *
     * @param world the world to decache
     */
    @InternalUseOnly
    public static void remove(World world) {
        worldExecutor().removeAssignment(world);
    }

    /**
     * Gets the executor for the entity thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static ExecutorFactory<Entity> entityExecutor() {
        return entities;
    }


    /**
     * Gets the executor for the player thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static ExecutorFactory<Player> playerExecutor() {
        return players;
    }


    /**
     * Gets the executor for the plugin thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static ExecutorFactory<TridentPlugin> pluginExecutor() {
        return plugins;
    }


    /**
     * Gets the executor for the world thread pool
     *
     * @return the executor
     */
    @InternalUseOnly
    public static ExecutorFactory<World> worldExecutor() {
        return worlds;
    }

    @Override
    public TaskExecutor entityThread(Entity entity) {
        return entityExecutor().assign(entity);
    }

    @Override
    public Collection<Entity> entities() {
        return entityExecutor().values();
    }

    @Override
    public TaskExecutor playerThread(Player player) {
        return playerExecutor().assign(player);
    }

    @Override
    public Collection<Player> players() {
        return playerExecutor().values();
    }

    @Override
    public TaskExecutor worldThread(World world) {
        return worldExecutor().assign(world);
    }

    @Override
    public Collection<World> worlds() {
        return worldExecutor().values();
    }

    @Override
    public <T> ExecutorFactory<T> executor(int threads) {
        return new ConcurrentTaskExecutor<>(threads);
    }
}
