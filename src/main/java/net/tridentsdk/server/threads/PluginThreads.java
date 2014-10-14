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

import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.plugin.TridentPlugin;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PluginThreads {
    static final ConcurrentTaskExecutor<TridentPlugin> THREAD_MAP = new ConcurrentTaskExecutor<>(2);
    static final ConcurrentCache<TridentPlugin, TaskExecutor> CACHE_MAP = new ConcurrentCache<>();

    static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

    private PluginThreads() {
    }

    /**
     * Gets the management tool for the plugin
     *
     * <p>May block the first call</p>
     *
     * @param plugin the plugin to find the wrapper for
     * @return the plugin thread handler
     */
    public static TaskExecutor pluginThreadHandle(final TridentPlugin plugin) {
        return PluginThreads.CACHE_MAP.retrieve(plugin, new Callable<TaskExecutor>() {
            @Override
            public TaskExecutor call() throws Exception {
                TaskExecutor executor = PluginThreads.THREAD_MAP.getScaledThread();
                PluginThreads.THREAD_MAP.assign(executor, plugin);

                return executor;
            }
        }, EntityThreads.SERVICE);
    }

    /**
     * Decaches the plugin handler from the mappings
     *
     * @param plugin the plugin to remove from the cache
     */
    public static void remove(TridentPlugin plugin) {
        PluginThreads.THREAD_MAP.removeAssignment(plugin);
        PluginThreads.CACHE_MAP.remove(plugin);
    }

    /**
     * Gets all of the thread plugin wrappers
     *
     * @return the values of the concurrent cache
     */
    public static Collection<TridentPlugin> wrappedPlayers() {
        return PluginThreads.THREAD_MAP.values();
    }
}
