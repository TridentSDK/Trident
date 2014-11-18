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
package net.tridentsdk.api.scheduling;

import net.tridentsdk.api.plugin.TridentPlugin;

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
