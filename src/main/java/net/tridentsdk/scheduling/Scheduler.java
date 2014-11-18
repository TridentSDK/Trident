/*
 *     TridentSDK - A Minecraft Server API
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
package net.tridentsdk.scheduling;

import net.tridentsdk.api.scheduling.TaskWrapper;
import net.tridentsdk.api.scheduling.TridentRunnable;
import net.tridentsdk.plugin.TridentPlugin;

public interface Scheduler {
    /**
     * Schedules a runnable to be run next tick
     *
     * @param plugin   The plugin that is creating this runnable
     * @param runnable the runnable to run
     * @return an integer representing the id of this runnable
     */
    TridentRunnable runTaskAsynchronously(TridentPlugin plugin, TridentRunnable runnable);

    TridentRunnable runTaskSynchronously(TridentPlugin plugin, TridentRunnable runnable);

    TridentRunnable runTaskAsyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay);

    TridentRunnable runTaskSyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay);

    TridentRunnable runTaskAsyncRepeating(TridentPlugin plugin, TridentRunnable runnable,
                                          long delay, long initialInterval);

    TridentRunnable runTaskSyncRepeating(TridentPlugin plugin, TridentRunnable runnable,
                                         long delay, long intialInterval);

    void cancel(int id);

    void cancel(TridentRunnable runnable);

    TaskWrapper wrapperById(int id);

    TaskWrapper wrapperByRun(TridentRunnable runnable);
}
