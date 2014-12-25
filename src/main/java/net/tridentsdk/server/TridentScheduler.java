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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TridentScheduler is a scheduling utility that is used to reflect ScheduledTasks at a given offset of the current
 * epoch of the server <p/> <p>The scheduler is designed to stage-heavy/run-light philosophy: most overhead in the
 * scheduler is to the run staging, which adds the ScheduledTask to the queue, and constructs the wrapper which assigns
 * the ScheduledTask executor and constructs the logic runnables. In contrast, running the wrapper would perform the
 * pre-constructed logic and mark the ScheduledTask then move on. This ensures that the ScheduledTask will be delayed
 * preferable when it is scheduled, instead of when it will be planned to run.</p> <p/> <p>Logic of ScheduledTask types:
 * <ul> <li>Run    - Call as soon as ticked, then cancelled</li> <li>Later  - an AtomicLong is incremented each tick,
 * when the long reaches the delay, the ScheduledTask is called and cancelled</li> <li>Repeat - an AtomicLong is
 * incremented each tick, when the long reaches the interval, the ScheduledTask is called, then the long is set to 0 and
 * continues.</li> </ul> <p/> The difference between sync and async ScheduledTasks is sync runs on the plugin thread
 * that is from the plugin scheduling the ScheduledTask. This is why a plugin object is required for ScheduledTask
 * scheduling. Async runs on one of the other 2 ScheduledTask execution threads (because there are 3 threads in the
 * scheduler).</p> <p/> <p>The benchmarks and testing units for the TridentScheduler can be found at:
 * http://git.io/nifjcg.</p> <p/> <p>Insertion logic places the ScheduledTask wrapped by the implementation of {@link
 * net.tridentsdk.concurrent.ScheduledTask} to perform the run logic and scheduling decisions plus automatic
 * ScheduledTask cancellation. Then, the overriden runnable with the ScheduledTask to be run is {@link
 * net.tridentsdk.concurrent.TridentRunnable#markSchedule(net.tridentsdk.concurrent.ScheduledTask)}ed to indicate the
 * ScheduledTask delegate is available.</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe public class TridentScheduler implements TaskFactory {
    private final Queue<ScheduledTaskImpl> taskList = new ConcurrentLinkedQueue<>();
    private final ExecutorFactory<ScheduledTaskImpl> taskExecutor = new ConcurrentTaskExecutor<>(3);

    public void tick() {
        for (ScheduledTaskImpl scheduledTask : taskList) {
            scheduledTask.run();
        }
    }

    private ScheduledTaskImpl doAdd(ScheduledTaskImpl wrap) {
        // Does not necessarily need to be atomic, as long as changes are visible
        // taskList is thread-safe
        // markSchedule sets an AtomicReference
        taskList.add(wrap);
        wrap.getRunnable().markSchedule(wrap);
        return wrap;
    }

    public void stop() {
        taskExecutor.shutdown();
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
    public ScheduledTask asyncRepeat(final TridentPlugin plugin, final TridentRunnable runnable, long delay, final
    long initialInterval) {
        // Schedule repeating ScheduledTask later
        return this.asyncLater(plugin, new TridentRunnable() {
            @Override
            public void run() {
                doAdd(new ScheduledTaskImpl(plugin, SchedulerType.ASYNC_REPEAT, runnable, initialInterval));
            }
        }, delay);
    }

    @Override
    public ScheduledTask syncRepeat(final TridentPlugin plugin, final TridentRunnable runnable, long delay, final
    long initialInterval) {
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

        private final AtomicLong interval = new AtomicLong();
        private final AtomicLong run = new AtomicLong(0);

        public ScheduledTaskImpl(TridentPlugin plugin, SchedulerType type, final TridentRunnable runnable, long step) {
            this.plugin = plugin;
            this.type = type;
            this.runnable = runnable;
            this.interval.set(step);

            switch (type) {
                case ASYNC_RUN:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            runnable.prerunSync();
                            runnable.run();
                            runnable.runAfterAsync();
                            cancel();
                        }
                    };
                    this.executor = taskExecutor.assign(this);
                    break;

                case ASYNC_LATER:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            // May be over if the interval set lower
                            if (run.get() >= interval.get()) {
                                runnable.prerunSync();
                                runnable.run();
                                runnable.runAfterAsync();
                                cancel();
                            }
                            run.incrementAndGet();
                        }
                    };
                    this.executor = taskExecutor.assign(this);
                    break;

                case ASYNC_REPEAT:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            // May be over if the interval set lower
                            if (run.get() >= interval.get()) {
                                run.set(0);

                                runnable.prerunSync();
                                runnable.run();
                                runnable.runAfterAsync();
                            }

                            run.incrementAndGet();
                        }
                    };
                    this.executor = taskExecutor.assign(this);
                    break;

                case SYNC_RUN:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            runnable.prerunSync();
                            runnable.run();
                            runnable.runAfterSync();
                            cancel();
                        }
                    };
                    this.executor = plugin.getExecutor();
                    break;

                case SYNC_LATER:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            // May be over if the interval set lower
                            if (run.get() >= interval.get()) {
                                runnable.prerunSync();
                                runnable.run();
                                runnable.runAfterSync();
                                cancel();
                            }
                            run.incrementAndGet();
                        }
                    };
                    this.executor = plugin.getExecutor();
                    break;

                case SYNC_REPEAT:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            // May be over if the interval set lower
                            if (run.get() >= interval.get()) {
                                run.set(0);

                                runnable.prerunSync();
                                runnable.run();
                                runnable.runAfterSync();
                            }
                            run.incrementAndGet();
                        }
                    };
                    this.executor = plugin.getExecutor();
                    break;

                default:
                    this.runner = null;
                    this.executor = null;
            }
        }

        @Override
        public long getInterval() {
            return this.interval.get();
        }

        @Override
        public void setInterval(long interval) {
            this.interval.set(interval);
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
        public TridentPlugin getPlugin() {
            return this.plugin;
        }

        @Override
        public void cancel() {
            taskList.remove(this);
        }

        @Override
        public void run() {
            this.executor.addTask(this.runner);
        }
    }
}
