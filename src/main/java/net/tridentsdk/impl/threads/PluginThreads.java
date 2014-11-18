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

import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.plugin.TridentPlugin;

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
     * <p/>
     * <p>May block the first call</p>
     *
     * @param plugin the plugin to find the wrapper for
     * @return the plugin thread handler
     */
    public static TaskExecutor pluginThreadHandle(final TridentPlugin plugin) {
        return CACHE_MAP.retrieve(plugin, new Callable<TaskExecutor>() {
            @Override
            public TaskExecutor call() throws Exception {
                TaskExecutor executor = THREAD_MAP.getScaledThread();
                return THREAD_MAP.assign(executor, plugin);
            }
        }, EntityThreads.SERVICE);
    }

    /**
     * Decaches the plugin handler from the mappings
     *
     * @param plugin the plugin to remove from the cache
     */
    public static void remove(TridentPlugin plugin) {
        THREAD_MAP.removeAssignment(plugin);
        CACHE_MAP.remove(plugin);
    }

    /**
     * Gets all of the thread plugin wrappers
     *
     * @return the values of the concurrent cache
     */
    public static Collection<TridentPlugin> wrappedPlugins() {
        return THREAD_MAP.values();
    }
}
