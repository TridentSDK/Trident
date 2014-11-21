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

import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.plugin.TridentPlugin;

import java.util.Collection;

public final class PluginThreads {
    static final ConcurrentTaskExecutor<TridentPlugin> THREAD_MAP = new ConcurrentTaskExecutor<>(2);

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
        return THREAD_MAP.assign(plugin);
    }

    /**
     * Decaches the plugin handler from the mappings
     *
     * @param plugin the plugin to remove from the cache
     */
    public static void remove(TridentPlugin plugin) {
        THREAD_MAP.removeAssignment(plugin);
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
