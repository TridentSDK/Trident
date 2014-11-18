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

import java.util.concurrent.atomic.AtomicBoolean;

public interface TaskWrapper extends Runnable {
    /**
     * Interval is the ticks left of a specific action for repeating and delayed tasks
     *
     * <p>For delayed tasks, the interval is the amount of ticks delay, and for repeating, interval is the amount
     * of ticks between each repeat of the task.</p>
     *
     * <p>Setting the interval will reset the ticks already accumulated by the task</p>
     *
     * @param interval the interval to set the task to
     */
    void setInterval(long interval);

    long getInterval();

    SchedulerType getType();

    TridentRunnable getRunnable();

    AtomicBoolean getRan();

    TridentPlugin getPlugin();
}
