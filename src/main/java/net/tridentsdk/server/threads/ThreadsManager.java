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

import net.tridentsdk.api.docs.InternalUseOnly;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.factory.ExecutorFactory;
import net.tridentsdk.api.factory.Factories;
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
    static final ExecutorFactory<Entity> entities = Factories.threads().executor(2);
    static final ExecutorFactory<Player> players = Factories.threads().executor(4);
    static final ExecutorFactory<TridentPlugin> plugins = Factories.threads().executor(2);
    static final ExecutorFactory<World> worlds = Factories.threads().executor(4);

    /**
     * Stops all the executors and clears all caches of concurrent threads
     */
    @InternalUseOnly
    public static void stopAll() {
        BackgroundTaskExecutor.SERVICE.shutdownNow();
        MainThread.getInstance().interrupt();
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
    public TaskExecutor pluginThread(TridentPlugin plugin) {
        return plugins.assign(plugin);
    }

    @Override
    public Collection<TridentPlugin> plugins() {
        return plugins.values();
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
