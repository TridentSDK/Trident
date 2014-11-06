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
package net.tridentsdk.server;

import net.tridentsdk.api.scheduling.Scheduler;
import net.tridentsdk.api.scheduling.TaskWrapper;
import net.tridentsdk.api.scheduling.TridentRunnable;
import net.tridentsdk.api.scheduling.Type;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import net.tridentsdk.server.threads.PluginThreads;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TridentScheduler is a scheduling utility that is used to execute tasks at a given offset of the current epoch of the
 * server
 *
 * <p>The scheduler is designed to stage-heavy/run-light philosophy: most overhead in the scheduler is to the run
 * staging, which adds the task to the queue, and constructs the wrapper which assigns the task executor and constructs
 * the logic runnables. In contrast, running the wrapper would perform the pre-constructed logic and mark the task then
 * move on. This ensures that the task will be delayed preferable when it is scheduled, instead of when it will be
 * planned to run.</p>
 *
 * <p>The scheduler is also designed around a double-ended queue. Every tick, two iterators transverse the task queue
 * and move the logic and handling to the threads specified during schedule. One iterator starts at the head of the
 * queue and the other iterator starts at the tail of the queue, moving "towards" each other. Each iterator will
 * transverse the entire list as it is unsure whether the task tick will move on before the second iterator gets to the
 * task. This can result in tasks going twice as fast as intended, as the double iterator will go over every task before
 * the tick continues, or result in partial completion of the task list when the loop returns after task intersects</p>
 *
 * <p>Logic of task types:
 * <ul>
 *     <li>Run    - Call as soon as ticked, then cancelled</li>
 *     <li>Later  - an AtomicLong is incremented each tick, when the long reaches the delay, the task is called and
 *     cancelled</li>
 *     <li>Repeat - an AtomicLong is incremented each tick, when the long reaches the interval, the task is called, then
 *     the long is set to 0 and continues.</li>
 * </ul>
 *
 * The difference between sync and async tasks is sync runs on the plugin thread that is from the plugin scheduling
 * the task. This is why a plugin object is required for task scheduling. Async runs on one of the other 2 task
 * execution threads (because there are 4 threads in the scheduler: 2 threads to run the double-iterator, and 2 task
 * executors).</p>
 *
 * <p>The tick method should never fall behind. The tasks are handled on a run thread, and ticks are staging operations
 * to the iteration executors. Synchronous tasks are executed on the plugin thread of the scheduling plugin, and
 * asynchronous tasks are scheduled on a separate executor contained internally in the scheduler.</p>
 *
 * <p>The benchmarks and testing units for the TridentScheduler can be found at: http://git.io/nifjcg.</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class TridentScheduler implements Scheduler {
    private final Runnable INVERSE_RUN = new Runnable() {
        @Override
        public void run() {
            for (Iterator<TaskWrapper> iterator = taskList.descendingIterator(); iterator.hasNext(); ) {
                TaskWrapper task = iterator.next();
                if (!task.getRan().compareAndSet(true, false))
                    task.run();
            }
        }
    };
    private final Runnable FORWARD_RUN = new Runnable() {
        @Override
        public void run() {
            for (TaskWrapper task : taskList) {
                if (!task.getRan().compareAndSet(true, false))
                    task.run();
            }
        }
    };

    private final Deque<TaskWrapper> taskList = new ConcurrentLinkedDeque<>();
    private final ConcurrentTaskExecutor<TaskWrapper> concurrentTaskExecutor = new ConcurrentTaskExecutor<>(2);
    private final ConcurrentTaskExecutor<TaskWrapper> taskQueue = new ConcurrentTaskExecutor<>(2);

    public void tick() {
        List<TaskExecutor> executors = concurrentTaskExecutor.threadList();
        for (int i = 0; i < executors.size(); i++) {
            TaskExecutor ex = executors.get(i);
            if (i % 2 == 0) ex.addTask(INVERSE_RUN);
            else ex.addTask(FORWARD_RUN);
        }
    }

    @Override
    public TridentRunnable runTaskAsynchronously(TridentPlugin plugin, TridentRunnable runnable) {
        return this.doAdd(new TaskWrapperImpl(plugin, Type.ASYNC_RUN, runnable, -1));
    }

    @Override
    public TridentRunnable runTaskSynchronously(TridentPlugin plugin, TridentRunnable runnable) {
        return this.doAdd(new TaskWrapperImpl(plugin, Type.SYNC_RUN, runnable, -1));
    }

    @Override
    public TridentRunnable runTaskAsyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        return this.doAdd(new TaskWrapperImpl(plugin, Type.ASYNC_LATER, runnable, delay));
    }

    @Override
    public TridentRunnable runTaskSyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        return this.doAdd(new TaskWrapperImpl(plugin, Type.SYNC_LATER, runnable, delay));
    }

    @Override
    public TridentRunnable runTaskAsyncRepeating(final TridentPlugin plugin, final TridentRunnable runnable, long delay,
                                                 final long initialInterval) {
        // Schedule repeating task later
        return this.runTaskAsyncLater(plugin, new TridentRunnable() {
            @Override
            public void run() {
                doAdd(new TaskWrapperImpl(plugin, Type.ASYNC_REPEAT, runnable, initialInterval));
            }
        }, delay);
    }

    @Override
    public TridentRunnable runTaskSyncRepeating(final TridentPlugin plugin, final TridentRunnable runnable, long delay,
                                                final long initialInterval) {
        // Schedule repeating task later
        return this.runTaskSyncLater(plugin, new TridentRunnable() {
            @Override
            public void run() {
                doAdd(new TaskWrapperImpl(plugin, Type.SYNC_REPEAT, runnable, initialInterval));
            }
        }, delay);
    }

    private TridentRunnable doAdd(TaskWrapper wrap) {
        taskList.add(wrap);
        return wrap.getRunnable();
    }

    @Override
    public void cancel(int id) {
        for (TaskWrapper wrapper : taskList)
            if (wrapper.getRunnable().getId() == id) taskList.remove(wrapper);
    }

    @Override
    public void cancel(TridentRunnable runnable) {
        for (TaskWrapper wrapper : taskList)
            if (wrapper.getRunnable().equals(runnable)) taskList.remove(wrapper);
    }

    @Override
    public TaskWrapper wrapperById(int i) {
        for (TaskWrapper wrapper : taskList)
            if (wrapper.getRunnable().getId() == i)
                return wrapper;
        return null;
    }

    @Override public TaskWrapper wrapperByRun(TridentRunnable runnable) {
        for (TaskWrapper wrapper : taskList)
            if (wrapper.getRunnable().equals(runnable))
                return wrapper;
        return null;
    }

    public void stop() {
        concurrentTaskExecutor.shutdown();
        taskQueue.shutdown();
    }

    public class TaskWrapperImpl implements TaskWrapper {
        private final TridentPlugin plugin;
        private final Type type;
        private final AtomicLong interval = new AtomicLong(0);
        private final AtomicLong run = new AtomicLong(0);
        private final TridentRunnable runnable;

        private final Runnable runner;
        private final AtomicBoolean ran = new AtomicBoolean();
        // This field is volatile because the two task loops are executed by
        // different threads in the ConcurrentTaskExecutor

        private final TaskExecutor executor;

        public TaskWrapperImpl(TridentPlugin plugin, Type type, final TridentRunnable runnable, long step) {
            this.plugin = plugin;
            this.type = type;
            this.runnable = runnable;
            this.interval.set(step);

            switch (type) {
                case ASYNC_RUN:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            runnable.run();
                            cancel(runnable);
                            ran.set(true);
                        }
                    };
                    this.executor = taskQueue.assign(taskQueue.getScaledThread(), this);
                    break;
                case ASYNC_LATER:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            if (run.get() == interval.get()) {
                                runnable.run();
                                cancel(runnable);
                            }

                            run.incrementAndGet();
                            ran.set(true);
                        }
                    };
                    this.executor = taskQueue.assign(taskQueue.getScaledThread(), this);
                    break;
                case ASYNC_REPEAT:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            if (run.compareAndSet(interval.get(), 0)) runnable.run();

                            run.incrementAndGet();
                            ran.set(true);
                        }
                    };
                    this.executor = taskQueue.assign(taskQueue.getScaledThread(), this);
                    break;
                case SYNC_RUN:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            runnable.run();
                            cancel(runnable);
                            ran.set(true);
                        }
                    };
                    this.executor = PluginThreads.pluginThreadHandle(plugin);
                    break;
                case SYNC_LATER:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            if (run.get() == interval.get()) {
                                runnable.run();
                                cancel(runnable);
                            }

                            run.incrementAndGet();
                            ran.set(true);
                        }
                    };
                    this.executor = PluginThreads.pluginThreadHandle(plugin);
                    break;
                case SYNC_REPEAT:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            if (run.compareAndSet(interval.get(), 0))
                                runnable.run();

                            run.incrementAndGet();
                            ran.set(true);
                        }
                    };
                    this.executor = PluginThreads.pluginThreadHandle(plugin);
                    break;
                default:
                    this.runner = null;
                    this.executor = null;
            }
        }

        @Override
        public void setInterval(long interval) {
            this.interval.set(interval);
            this.run.set(0);
        }

        @Override 
        public long getInterval() {
            return this.interval.get();
        }

        @Override
        public Type getType() {
            return this.type;
        }

        @Override
        public TridentRunnable getRunnable() {
            return this.runnable;
        }

        @Override
        public AtomicBoolean getRan() {
            return ran;
        }

        @Override
        public TridentPlugin getPlugin() {
            return this.plugin;
        }

        @Override
        public void run() {
            this.executor.addTask(this.runner);
        }
    }
}
