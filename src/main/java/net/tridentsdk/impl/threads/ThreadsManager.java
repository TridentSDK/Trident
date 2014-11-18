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

import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.threads.ThreadProvider;
import net.tridentsdk.api.world.World;
import net.tridentsdk.api.plugin.TridentPlugin;

/**
 * Handles the majority of the lifecycle for the threads
 *
 * @author The TridentSDK Team
 */
public final class ThreadsManager implements ThreadProvider {
    /**
     * Stops all the executors and clears all caches of concurrent threads
     */
    public static void stopAll() {
        BackgroundTaskExecutor.SERVICE.shutdownNow();
        PlayerThreads.SERVICE.shutdownNow();

        PlayerThreads.SERVICE.shutdownNow();
        PlayerThreads.THREAD_MAP.shutdown();

        PluginThreads.SERVICE.shutdownNow();
        PluginThreads.THREAD_MAP.shutdown();

        EntityThreads.SERVICE.shutdownNow();
        EntityThreads.THREAD_MAP.shutdown();

        WorldThreads.SERVICE.shutdownNow();
        WorldThreads.THREAD_MAP.shutdown();

        MainThread.getInstance().interrupt();
    }

    @Override
    public TaskExecutor provideEntityThread(Entity entity) {
        return EntityThreads.entityThreadHandle(entity);
    }

    @Override
    public TaskExecutor providePlayerThread(Player player) {
        return PlayerThreads.clientThreadHandle(player);
    }

    @Override
    public TaskExecutor providePluginThread(TridentPlugin plugin) {
        return PluginThreads.pluginThreadHandle(plugin);
    }

    @Override
    public TaskExecutor provideWorldThread(World world) {
        return WorldThreads.worldThreadHandle(world);
    }
}
