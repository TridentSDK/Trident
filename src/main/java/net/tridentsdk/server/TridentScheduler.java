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

import com.google.common.collect.Iterators;
import net.tridentsdk.api.scheduling.Scheduler;
import net.tridentsdk.api.scheduling.TridentRunnable;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.plugin.annotation.PluginDescription;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import net.tridentsdk.server.threads.PluginThreads;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class TridentScheduler implements Scheduler {
    private static final Runnable INVERSE_RUN = new Runnable() {
        @Override
        public void run() {
            for (Iterator<TaskWrapper> iterator = reverse.get(); iterator.hasNext();) {
                TaskWrapper task = iterator.next();
                if (task == null) continue;
                if (!task.hasRan()) task.run();
                else task.setRan(false);
            }
        }
    };
    private static final Runnable FORWARD_RUN = new Runnable() {
        @Override
        public void run() {
            for (Iterator<TaskWrapper> iterator = forward.get(); iterator.hasNext();) {
                TaskWrapper task = iterator.next();
                if (task == null) continue;
                if (!task.hasRan()) task.run();
                else task.setRan(false);
            }
        }
    };

    private static final Deque<TaskWrapper> taskList = new ConcurrentLinkedDeque<>();
    private static final ConcurrentTaskExecutor<TaskWrapper> concurrentTaskExecutor = new ConcurrentTaskExecutor<>(2);
    private static final ConcurrentTaskExecutor<TaskWrapper> taskQueue = new ConcurrentTaskExecutor<>(2);

    private static final AtomicReference<Iterator<TaskWrapper>> reverse = new AtomicReference<>();
    private static final AtomicReference<Iterator<TaskWrapper>> forward = new AtomicReference<>();

    public static void main(String... args) throws InterruptedException {
        TridentScheduler scheduler = new TridentScheduler();
        for (int i = 0; i < 100; i++) {
            @PluginDescription(name = "LOLCODE")
            class PluginImpl extends TridentPlugin {}

            scheduler.runTaskSyncLater(new PluginImpl(), new TridentRunnable() {
                @Override public void run() {
                    System.out.println("LOL");
                }
            }, 10L);
        }
        for (int i = 0; i < 100000000; i++) {
            Thread.sleep(1000);
            scheduler.tick();
        }

        scheduler.stop();
    }

    public void tick() {
        List<TaskExecutor> executors = concurrentTaskExecutor.threadList();
        for (int i = 0; i < executors.size(); i++) {
            TaskExecutor ex = executors.get(i);
            if (i % 2 == 0) {
                ex.addTask(INVERSE_RUN);
            } else {
                ex.addTask(FORWARD_RUN);
            }
        }
    }

    @Override
    public TridentRunnable runTaskAsynchronously(TridentPlugin plugin, TridentRunnable runnable) {
        Type type = Type.ASYNC_RUN;
        return this.doAdd(new TaskWrapper(plugin, type, runnable, -1));
    }

    @Override
    public TridentRunnable runTaskSynchronously(TridentPlugin plugin, TridentRunnable runnable) {
        Type type = Type.SYNC_RUN;
        return this.doAdd(new TaskWrapper(plugin, type, runnable, -1));
    }

    @Override
    public TridentRunnable runTaskAsyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        Type type = Type.ASYNC_LATER;
        return this.doAdd(new TaskWrapper(plugin, type, runnable, delay));
    }

    @Override
    public TridentRunnable runTaskSyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        Type type = Type.SYNC_LATER;
        return this.doAdd(new TaskWrapper(plugin, type, runnable, delay));
    }

    @Override
    public TridentRunnable runTaskAsyncRepeating(final TridentPlugin plugin, final TridentRunnable runnable, long delay,
            final long initialInterval) {
        // Add repeating task later
        return this.runTaskAsyncLater(plugin, new TridentRunnable() {
            @Override
            public void run() {
                doAdd(new TaskWrapper(plugin, Type.ASYNC_REPEAT, runnable, initialInterval));
            }
        }, delay);
    }

    @Override
    public TridentRunnable runTaskSyncRepeating(final TridentPlugin plugin, final TridentRunnable runnable, long delay,
            final long initialInterval) {
        // Add repeating task later
        return this.runTaskSyncLater(plugin, new TridentRunnable() {
            @Override
            public void run() {
                doAdd(new TaskWrapper(plugin, Type.SYNC_REPEAT, runnable, initialInterval));
            }
        }, delay);
    }

    private TridentRunnable doAdd(TaskWrapper wrap) {
        taskList.add(wrap);
        this.update();
        return wrap.getRunnable();
    }

    private void update() {
        forward.set(Iterators.peekingIterator(taskList.iterator()));
        reverse.set(Iterators.peekingIterator(taskList.descendingIterator()));
    }

    @Override
    public void cancel(int id) {
        for (Iterator<TaskWrapper> iterator = forward.get(); iterator.hasNext();) {
            TaskWrapper wrapper = iterator.next();
            if (wrapper == null) continue;
            if (wrapper.getRunnable().getId() == id) {
                taskList.remove(wrapper);
                this.update();
            }
        }
    }

    @Override
    public void cancel(TridentRunnable runnable) {
        for (Iterator<TaskWrapper> iterator = forward.get(); iterator.hasNext();) {
            TaskWrapper wrapper = iterator.next();
            if (wrapper == null) continue;
            if (wrapper.getRunnable().equals(runnable)) {
                taskList.remove(wrapper);
                this.update();
            }
        }
    }

    public void stop() {
        concurrentTaskExecutor.shutdown();
        taskQueue.shutdown();
    }

    private class TaskWrapper implements Runnable {
        private final TridentPlugin plugin;
        private final Type type;
        private final AtomicLong run = new AtomicLong(0);
        private final TridentRunnable runnable;

        private final Runnable runner;
        private volatile boolean ran;

        private final TaskExecutor executor;

        public TaskWrapper(TridentPlugin plugin, Type type, final TridentRunnable runnable, final long interval) {
            this.plugin = plugin;
            this.type = type;
            this.runnable = runnable;

            switch (type) {
                case ASYNC_RUN:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            taskQueue.getScaledThread().addTask(runnable);
                            cancel(runnable);
                        }
                    };
                    this.executor = taskQueue.assign(taskQueue.getScaledThread(), this);
                    break;
                case ASYNC_LATER:
                    this.runner = new Runnable() {
                        @Override public void run() {
                            if (run.get() == interval) {
                                taskQueue.getScaledThread().addTask(runnable);
                                cancel(runnable);
                            } else {
                                run.incrementAndGet();
                            }
                        }
                    };
                    this.executor = taskQueue.assign(taskQueue.getScaledThread(), this);
                    break;
                case ASYNC_REPEAT:
                    this.runner = new Runnable() {
                        @Override public void run() {
                            if (run.compareAndSet(interval, 0)) {
                                taskQueue.getScaledThread().addTask(runnable);
                            } else {
                                run.incrementAndGet();
                            }
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
                        }
                    };
                    this.executor = PluginThreads.pluginThreadHandle(plugin);
                    break;
                case SYNC_LATER:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            if (run.get() == interval) {
                                runnable.run();
                                cancel(runnable);
                            } else {
                                run.incrementAndGet();
                            }
                        }
                    };
                    this.executor = PluginThreads.pluginThreadHandle(plugin);
                    break;
                case SYNC_REPEAT:
                    this.runner = new Runnable() {
                        @Override
                        public void run() {
                            if (run.compareAndSet(interval, 0)) {
                                runnable.run();
                            } else {
                                run.incrementAndGet();
                            }
                        }
                    };
                    this.executor = PluginThreads.pluginThreadHandle(plugin);
                    break;
                default:
                    this.runner = null;
                    this.executor = null;
            }
        }

        public Type getType() {
            return this.type;
        }

        public TridentRunnable getRunnable() {
            return this.runnable;
        }

        public boolean hasRan() {
            return this.ran;
        }

        public void setRan(boolean ran) {
            this.ran = ran;
        }

        @Override
        public void run() {
            this.executor.addTask(this.runnable);
        }
    }

    private enum Type {
        ASYNC_RUN, ASYNC_LATER, ASYNC_REPEAT, SYNC_RUN, SYNC_LATER, SYNC_REPEAT
    }
}