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
            Iterator<TaskWrapper> iterator = reverse.get();
            for (TaskWrapper task = null; iterator.hasNext(); task = iterator.next()) {
                if (task == null) continue;
                if (!task.hasRan()) task.run();
                System.out.println("Ran by inverse run");
            }
        }
    };
    private static final Runnable FORWARD_RUN = new Runnable() {
        @Override
        public void run() {
            Iterator<TaskWrapper> iterator = forward.get();
            for (TaskWrapper task = null; iterator.hasNext(); task = iterator.next()) {
                if (task == null) continue;
                if (!task.hasRan()) task.run();
                System.out.println("Ran by forward run");
            }
        }
    };

    private static final Deque<TaskWrapper> taskList = new ConcurrentLinkedDeque<>();
    private static final ConcurrentTaskExecutor<TaskWrapper> concurrentTaskExecutor = new ConcurrentTaskExecutor<>(2);
    private static final ConcurrentTaskExecutor<TaskWrapper> taskQueue = new ConcurrentTaskExecutor<>(2);

    private static final AtomicReference<Iterator<TaskWrapper>> reverse = new AtomicReference<>();
    private static final AtomicReference<Iterator<TaskWrapper>> forward = new AtomicReference<>();

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
        for (TaskWrapper wrapper = null; forward.get().hasNext(); wrapper = forward.get().next()) {
            if (wrapper == null) continue;
            if (wrapper.getRunnable().getId() == id) {
                taskList.remove(wrapper);
                this.update();
            }
        }
    }

    @Override
    public void cancel(TridentRunnable runnable) {
        for (TaskWrapper wrapper = null; forward.get().hasNext(); wrapper = forward.get().next()) {
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
        private final long interval;
        private final TridentRunnable runnable;

        private final Runnable ifUsed;
        private volatile boolean ran;

        public TaskWrapper(TridentPlugin plugin, Type type, final TridentRunnable runnable, final long interval) {
            this.plugin = plugin;
            this.type = type;
            this.interval = interval;
            this.runnable = runnable;

            switch (type) {
                case SYNC_RUN:
                    this.ifUsed = new Runnable() {
                        @Override
                        public void run() {
                            runnable.run();
                            cancel(runnable);
                        }
                    };
                    break;
                case SYNC_LATER:
                    this.ifUsed = new Runnable() {
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
                    break;
                case SYNC_REPEAT:
                    this.ifUsed = new Runnable() {
                        @Override
                        public void run() {
                            if (run.compareAndSet(interval, 0)) {
                                runnable.run();
                            } else {
                                run.incrementAndGet();
                            }
                        }
                    };
                    break;
                default:
                    this.ifUsed = null;
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

        @Override
        public void run() {
            switch (this.type) {
                case ASYNC_RUN:
                    taskQueue.getScaledThread().addTask(this.runnable);
                    cancel(this.runnable);
                    break;
                case ASYNC_LATER:
                    if (this.run.get() == this.interval) {
                        taskQueue.getScaledThread().addTask(this.runnable);
                        cancel(this.runnable);
                    } else {
                        this.run.incrementAndGet();
                    }
                    break;
                case ASYNC_REPEAT:
                    if (this.run.compareAndSet(this.interval, 0)) {
                        taskQueue.getScaledThread().addTask(this.runnable);
                    } else {
                        this.run.incrementAndGet();
                    }
                    break;
                case SYNC_RUN:
                    PluginThreads.pluginThreadHandle(this.plugin).addTask(this.ifUsed);
                    break;
                case SYNC_LATER:
                    PluginThreads.pluginThreadHandle(this.plugin).addTask(this.ifUsed);
                    break;
                case SYNC_REPEAT:
                    PluginThreads.pluginThreadHandle(this.plugin).addTask(this.ifUsed);
                    break;
            }
            this.ran = true;
        }
    }

    private enum Type {
        ASYNC_RUN, ASYNC_LATER, ASYNC_REPEAT, SYNC_RUN, SYNC_LATER, SYNC_REPEAT
    }
}