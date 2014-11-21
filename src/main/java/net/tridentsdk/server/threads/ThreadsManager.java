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

import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.factory.ExecutorFactory;
import net.tridentsdk.api.factory.Factories;
import net.tridentsdk.api.factory.ThreadFactory;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.world.World;
import net.tridentsdk.plugin.TridentPlugin;

/**
 * Handles the majority of the lifecycle for the threads
 *
 * @author The TridentSDK Team
 */
public final class ThreadsManager implements ThreadFactory {
    static {
        Factories.init(new ThreadsManager());
    }

    /**
     * Stops all the executors and clears all caches of concurrent threads
     */
    public static void stopAll() {
        BackgroundTaskExecutor.SERVICE.shutdownNow();
        PlayerThreads.SERVICE.shutdownNow();

        PlayerThreads.SERVICE.shutdownNow();
        PlayerThreads.THREAD_MAP.shutdown();

        PluginThreads.THREAD_MAP.shutdown();

        EntityThreads.SERVICE.shutdownNow();
        EntityThreads.THREAD_MAP.shutdown();

        WorldThreads.SERVICE.shutdownNow();
        WorldThreads.THREAD_MAP.shutdown();

        MainThread.getInstance().interrupt();
    }

    @Override
    public TaskExecutor entityThread(Entity entity) {
        return EntityThreads.entityThreadHandle(entity);
    }

    @Override
    public TaskExecutor playerThread(Player player) {
        return PlayerThreads.clientThreadHandle(player);
    }

    @Override
    public TaskExecutor pluginThread(TridentPlugin plugin) {
        return PluginThreads.pluginThreadHandle(plugin);
    }

    @Override
    public TaskExecutor worldThread(World world) {
        return WorldThreads.worldThreadHandle(world);
    }

    @Override
    public <T> ExecutorFactory<T> executor(int threads) {
        return new ConcurrentTaskExecutor<>(threads);
    }
}
