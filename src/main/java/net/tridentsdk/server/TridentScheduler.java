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

import net.tridentsdk.api.factory.Factories;
import net.tridentsdk.api.factory.TaskFactory;
import net.tridentsdk.api.scheduling.SchedulerType;
import net.tridentsdk.api.scheduling.Task;
import net.tridentsdk.api.scheduling.TridentRunnable;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TridentScheduler is a scheduling utility that is used to reflect tasks at a given offset of the current epoch of the
 * server
 *
 * <p>The scheduler is designed to stage-heavy/run-light philosophy: most overhead in the scheduler is to the run
 * staging, which adds the task to the queue, and constructs the wrapper which assigns the task executor and constructs
 * the logic runnables. In contrast, running the wrapper would perform the pre-constructed logic and mark the task then
 * move on. This ensures that the task will be delayed preferable when it is scheduled, instead of when it will be
 * planned to run.</p>
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
 * execution threads (because there are 3 threads in the scheduler).</p>
 *
 * <p>The benchmarks and testing units for the TridentScheduler can be found at: http://git.io/nifjcg.</p>
 *
 * <p>Insertion logic places the task wrapped by the implementation of {@link net.tridentsdk.api.scheduling.Task} to
 * perform the run logic and scheduling decisions plus automatic task cancellation. Then, the overriden runnable with
 * the task to be run is
 * {@link net.tridentsdk.api.scheduling.TridentRunnable#markSchedule(net.tridentsdk.api.scheduling.Task)}ed to indicate
 * the task delegate is available.</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class TridentScheduler implements TaskFactory {
    private final Queue<TaskImpl> taskList = new ConcurrentLinkedQueue<>();
    private final ConcurrentTaskExecutor<TaskImpl> taskQueue = new ConcurrentTaskExecutor<>(3);

    public void tick() {
        for (TaskImpl task : taskList) {
            if (!task.getRan().compareAndSet(true, false))
                task.run();
        }
    }

    private TaskImpl doAdd(TaskImpl wrap) {
        // Does not necessarily need to be atomic, as long as changes are visible
        // taskList is thread-safe
        // markSchedule sets an AtomicReference
        taskList.add(wrap);
        wrap.getRunnable().markSchedule(wrap);
        return wrap;
    }

    public void stop() {
        taskQueue.shutdown();
    }

    @Override
    public Task asyncRun(TridentPlugin plugin, TridentRunnable runnable) {
        return this.doAdd(new TaskImpl(plugin, SchedulerType.ASYNC_RUN, runnable, -1));
    }

    @Override
    public Task syncRun(TridentPlugin plugin, TridentRunnable runnable) {
        return this.doAdd(new TaskImpl(plugin, SchedulerType.SYNC_RUN, runnable, -1));
    }

    @Override
    public Task asyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        return this.doAdd(new TaskImpl(plugin, SchedulerType.ASYNC_LATER, runnable, delay));
    }

    @Override
    public Task syncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        return this.doAdd(new TaskImpl(plugin, SchedulerType.SYNC_LATER, runnable, delay));
    }

    @Override
    public Task asyncRepeat(final TridentPlugin plugin, final TridentRunnable runnable, long delay, final long initialInterval) {
        // Schedule repeating task later
        return this.asyncLater(plugin, new TridentRunnable() {
            @Override
            public void run() {
                doAdd(new TaskImpl(plugin, SchedulerType.ASYNC_REPEAT, runnable, initialInterval));
            }
        }, delay);
    }

    @Override
    public Task syncRepeat(final TridentPlugin plugin, final TridentRunnable runnable, long delay, final long initialInterval) {
        // Schedule repeating task later
        return this.syncLater(plugin, new TridentRunnable() {
            @Override
            public void run() {
                doAdd(new TaskImpl(plugin, SchedulerType.SYNC_REPEAT, runnable, initialInterval));
            }
        }, delay);
    }

    private class TaskImpl implements Task {
        private final TridentPlugin plugin;
        private final SchedulerType type;
        private final AtomicLong interval = new AtomicLong(0);
        private final AtomicLong run = new AtomicLong(0);
        private final TridentRunnable runnable;

        private final Runnable runner;
        private final AtomicBoolean ran = new AtomicBoolean();
        // This field is volatile because the two task loops are executed by
        // different threads in the ConcurrentTaskExecutor

        private final TaskExecutor executor;

        public TaskImpl(TridentPlugin plugin, SchedulerType type, final TridentRunnable runnable, long step) {
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
                            cancel();
                        }
                    };
                    this.executor = taskQueue.assign(this);
                    break;

                case ASYNC_LATER:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            if (run.get() == interval.get()) {
                                runnable.run();
                                cancel();
                            }
                            run.incrementAndGet();
                        }
                    };
                    this.executor = taskQueue.assign(this);
                    break;

                case ASYNC_REPEAT:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            if (run.compareAndSet(interval.get(), 0)) runnable.run();

                            run.incrementAndGet();
                        }
                    };
                    this.executor = taskQueue.assign(this);
                    break;

                case SYNC_RUN:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            runnable.run();
                            cancel();
                        }
                    };
                    this.executor = Factories.threads().pluginThread(plugin);
                    break;

                case SYNC_LATER:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            if (run.get() == interval.get()) {
                                runnable.run();
                                cancel();
                            }
                            run.incrementAndGet();
                        }
                    };
                    this.executor = Factories.threads().pluginThread(plugin);
                    break;

                case SYNC_REPEAT:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            if (run.compareAndSet(interval.get(), 0))
                                runnable.run();
                            run.incrementAndGet();
                        }
                    };
                    this.executor = Factories.threads().pluginThread(plugin);
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
        public SchedulerType getType() {
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
        public void cancel() {
            taskList.remove(this);
        }

        @Override
        public void run() {
            // Again, does not necessarily need to be atomic
            // Can only be run by a single thread at once because ran guaranteed to be checked
            this.ran.set(true); // Prevent the other thread from interfering
            this.runnable.prerunSync();
            this.executor.addTask(this.runner);
            if (type.name().contains("ASYNC")) {
                this.runnable.runAfterAsync();
            } else this.runnable.runAfterSync();
        }
    }
}
