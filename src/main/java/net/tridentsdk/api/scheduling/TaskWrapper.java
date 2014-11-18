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
