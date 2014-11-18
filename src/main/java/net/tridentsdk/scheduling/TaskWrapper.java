package net.tridentsdk.scheduling;

import net.tridentsdk.plugin.TridentPlugin;

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
