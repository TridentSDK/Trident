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

package net.tridentsdk.server;

import net.tridentsdk.concurrent.ScheduledTask;
import net.tridentsdk.concurrent.SchedulerType;
import net.tridentsdk.concurrent.TaskExecutor;
import net.tridentsdk.concurrent.TridentRunnable;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.factory.TaskFactory;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * TridentScheduler is a scheduling utility that is used to reflect ScheduledTasks at a given offset of the current
 * epoch of the server
 *
 * <p>The scheduler is designed to stage-heavy/run-light philosophy: most overhead in the
 * scheduler is to the run staging, which adds the ScheduledTask to the queue, and constructs the wrapper which assigns
 * the ScheduledTask executor and constructs the logic runnables. In contrast, running the wrapper would perform the
 * pre-constructed logic and mark the ScheduledTask then move on. This ensures that the ScheduledTask will be delayed
 * preferable when it is scheduled, instead of when it will be planned to run.</p>
 *
 * <p>Logic of ScheduledTask types:
 * <ul>
 * <li>Run    - Call as soon as ticked, then cancelled</li>
 * <li>Later  - an AtomicLong is incremented each tick when the long reaches the delay, the ScheduledTask is
 * called and cancelled</li>
 * <li>Repeat - an AtomicLong is incremented each tick, when the long reaches the interval, the ScheduledTask is
 * called, then the long is set to 0 and continues.</li>
 * </ul>
 *
 * The difference between sync and async ScheduledTasks is sync runs on the plugin thread
 * that is from the plugin scheduling the ScheduledTask. This is why a plugin object is required for ScheduledTask
 * scheduling. Async runs on one of the other 2 ScheduledTask execution threads (because there are 3 threads in the
 * scheduler).</p>
 *
 * <p>The benchmarks and testing units for the TridentScheduler can be found at:
 * http://git.io/nifjcg.</p>
 *
 * <p>Insertion logic places the ScheduledTask wrapped by the implementation of {@link
 * net.tridentsdk.concurrent.ScheduledTask} to perform the run logic and scheduling decisions plus automatic
 * ScheduledTask cancellation. Then, the overriden runnable with the ScheduledTask to be run is {@link
 * net.tridentsdk.concurrent.TridentRunnable#markSchedule(net.tridentsdk.concurrent.ScheduledTask)}ed to indicate the
 * ScheduledTask delegate is available.</p>
 *
 * <p>Thread safety is ensured a single iteration thread, the tick thread. Tasks added first put in the task list,
 * then the task is marked. The execution has a higher priority over the access to the task scheduling period. Also,
 * most tasks will be allowed to complete before any change is needed. Task execution occurs in a single thread,
 * the tick method adds to an executor which does not share the state of the task implementation.</p>
 *
 * <p>The scheduler is high performance due to lock-free execution. The internal task list is a
 * {@link java.util.concurrent.ConcurrentLinkedQueue}, iterated in the tick method which schedules a runnable assigned
 * to the task during construction. The most overhead occurs when the runnable is scheduled, and when the logic for
 * the scheduling method is decided during the task wrapper's construction.</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class TridentScheduler implements TaskFactory {
    private final Queue<ScheduledTaskImpl> taskList = new ConcurrentLinkedQueue<>();
    private final ExecutorFactory<?> taskExecutor = ConcurrentTaskExecutor.create(3, "Scheduler");

    private TridentScheduler() {
    }

    /**
     * Creates a new scheduler
     *
     * @return the new scheduler
     */
    public static TridentScheduler create() {
        return new TridentScheduler();
    }

    public void tick() {
        Iterator<ScheduledTaskImpl> iterator = taskList.iterator();
        for (; iterator.hasNext(); ) {
            iterator.next().run();
        }
    }

    private ScheduledTaskImpl doAdd(ScheduledTaskImpl wrap) {
        // Does not necessarily need to be atomic, as long as changes are visible
        // taskList is thread-safe
        // markSchedule sets an AtomicReference
        while (true) {
            boolean added = taskList.add(wrap);
            if (added) {
                wrap.runnable().markSchedule(wrap);
                return wrap;
            }
        }
    }

    public void shutdown() {
        taskExecutor.shutdown();
        taskList.clear();
    }

    @Override
    public ScheduledTask asyncRun(TridentPlugin plugin, TridentRunnable runnable) {
        return this.doAdd(new ScheduledTaskImpl(plugin, SchedulerType.ASYNC_RUN, runnable, -1));
    }

    @Override
    public ScheduledTask syncRun(TridentPlugin plugin, TridentRunnable runnable) {
        return this.doAdd(new ScheduledTaskImpl(plugin, SchedulerType.SYNC_RUN, runnable, -1));
    }

    @Override
    public ScheduledTask asyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        return this.doAdd(new ScheduledTaskImpl(plugin, SchedulerType.ASYNC_LATER, runnable, delay));
    }

    @Override
    public ScheduledTask syncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        return this.doAdd(new ScheduledTaskImpl(plugin, SchedulerType.SYNC_LATER, runnable, delay));
    }

    @Override
    public ScheduledTask asyncRepeat(final TridentPlugin plugin, final TridentRunnable runnable, long delay,
            final long initialInterval) {
        // Schedule repeating ScheduledTask later
        return this.asyncLater(plugin, new TridentRunnable() {
            @Override
            public void run() {
                doAdd(new ScheduledTaskImpl(plugin, SchedulerType.ASYNC_REPEAT, runnable, initialInterval));
            }
        }, delay);
    }

    @Override
    public ScheduledTask syncRepeat(final TridentPlugin plugin, final TridentRunnable runnable, long delay,
            final long initialInterval) {
        // Schedule repeating ScheduledTask later
        return this.syncLater(plugin, new TridentRunnable() {
            @Override
            public void run() {
                doAdd(new ScheduledTaskImpl(plugin, SchedulerType.SYNC_REPEAT, runnable, initialInterval));
            }
        }, delay);
    }

    private class ScheduledTaskImpl implements ScheduledTask {
        private final TridentPlugin plugin;
        private final SchedulerType type;
        private final TridentRunnable runnable;

        private final TaskExecutor executor;
        private final Runnable runner;

        private volatile long interval;
        private long run = 0L;

        public ScheduledTaskImpl(TridentPlugin plugin, SchedulerType type, final TridentRunnable runnable, long step) {
            this.plugin = plugin;
            this.type = type;
            this.runnable = runnable;
            this.interval = step;

            if (type.name().contains("ASYNC")) {
                this.executor = taskExecutor.scaledThread();
                if (!type.name().contains("REPEAT")) {
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            runnable.beforeRun();
                            runnable.run();
                            runnable.afterAsyncRun();
                            cancel();
                        }
                    };
                } else {
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            runnable.beforeRun();
                            runnable.run();
                            runnable.afterAsyncRun();
                        }
                    };
                }
            } else {
                this.executor = plugin.executor();
                if (!type.name().contains("REPEAT")) {
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            runnable.beforeRun();
                            runnable.run();
                            runnable.afterSyncRun();
                            cancel();
                        }
                    };
                } else {
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            runnable.beforeRun();
                            runnable.run();
                            runnable.afterSyncRun();
                        }
                    };
                }
            }
        }

        @Override
        public long interval() {
            return this.interval;
        }

        @Override
        public void setInterval(long interval) {
            this.interval = interval;
        }

        @Override
        public SchedulerType type() {
            return this.type;
        }

        @Override
        public TridentRunnable runnable() {
            return this.runnable;
        }

        @Override
        public TridentPlugin owner() {
            return this.plugin;
        }

        @Override
        public void cancel() {
            taskList.remove(this);
        }

        @Override
        public void run() {
            switch (type) {
                case ASYNC_RUN:
                    this.executor.addTask(this.runner);
                    break;

                case ASYNC_LATER:
                    // Maybe over if the interval set lower
                    if (run >= interval)
                        this.executor.addTask(this.runner);
                    ++run;
                    break;

                case ASYNC_REPEAT:
                    // Maybe over if the interval set lower
                    if (run >= interval)
                        this.executor.addTask(this.runner);
                    ++run;
                    break;

                case SYNC_RUN:
                    this.executor.addTask(this.runner);
                    break;

                case SYNC_LATER:
                    // May be over if the interval set lower
                    if (run >= interval)
                        this.executor.addTask(this.runner);
                    ++run;
                    break;

                case SYNC_REPEAT:
                    // May be over if the interval set lower
                    if (run >= interval)
                        this.executor.addTask(this.runner);
                    ++run;
                    break;
                default:
                    throw new IllegalStateException("How did this happen?");
            }
        }
    }
}