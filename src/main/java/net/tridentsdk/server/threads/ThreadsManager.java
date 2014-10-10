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

import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.threads.ThreadProvider;
import net.tridentsdk.api.world.World;
import net.tridentsdk.plugin.TridentPlugin;

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

    @Override public TaskExecutor provideEntityThread(Entity entity) {
        return EntityThreads.entityThreadHandle(entity);
    }

    @Override public TaskExecutor providePlayerThread(Player player) {
        return PlayerThreads.clientThreadHandle(player);
    }

    @Override public TaskExecutor providePluginThread(TridentPlugin plugin) {
        return PluginThreads.pluginThreadHandle(plugin);
    }

    @Override public TaskExecutor provideWorldThread(World world) {
        return WorldThreads.worldThreadHandle(world);
    }
}
