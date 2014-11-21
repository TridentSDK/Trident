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
package net.tridentsdk.server.threads;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.tridentsdk.api.factory.ExecutorFactory;
import net.tridentsdk.api.perf.AddTakeQueue;
import net.tridentsdk.api.perf.DelegatedAddTakeQueue;
import net.tridentsdk.api.threads.TaskExecutor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread list to allow task execution in a shared thread scaled with removal
 *
 * <p>Allows assignment of a worker to the user</p>
 *
 * @param <Assignment> the assignment type, if used
 * @author The TridentSDK Team
 */
public class ConcurrentTaskExecutor<Assignment> implements ExecutorFactory<Assignment> {
    private final Map<Assignment, InnerThread> assignments = new HashMap<>();
    private final List<InnerThread> threads = new ArrayList<>();

    private final List<TaskExecutor> executors;

    /**
     * Create a new executor using the number of threads to scale
     *
     * @param scale the threads to use. Functionality unspecified if negative or {@code == 0}
     */
    public ConcurrentTaskExecutor(int scale) {
        for (int i = 0; i < scale; i++) this.threads.add(new InnerThread());
        this.executors = Lists.newArrayList(Iterators.transform(this.threads.iterator(), new Function
                <InnerThread, TaskExecutor>() {
            @Nullable
            @Override
            public TaskExecutor apply(@Nullable InnerThread innerThread) {
                return innerThread;
            }
        }));
    }

    public TaskExecutor scaledThread() {
        InnerThread lowest = null;
        for (InnerThread thread : this.threads) {
            if (lowest == null || thread.getCount().get() < lowest.getCount().get())
                lowest = thread;
        }

        return lowest;
    }

    public TaskExecutor assign(Assignment assignment) {
        TaskExecutor executor = this.assignments.get(assignment);
        if (executor == null) {
            InnerThread thread = (InnerThread) scaledThread();
            thread.getCount().incrementAndGet();
            this.assignments.put(assignment, thread);

            return thread;
        }

        return executor;
    }

    public void removeAssignment(Assignment assignment) {
        this.assignments.remove(assignment);
    }

    public Collection<Assignment> values() {
        return this.assignments.keySet();
    }

    public List<TaskExecutor> threadList() {
        return this.executors;
    }

    public void shutdown() {
        for (TaskExecutor thread : Sets.newHashSet(assignments.values()))
            thread.interrupt();
        this.assignments.clear();
    }

    private static final class InnerThread implements TaskExecutor {
        private final AddTakeQueue<Runnable> tasks = new DelegatedAddTakeQueue<Runnable>() {
            @Override protected BlockingQueue<Runnable> delegate() {
                return new LinkedBlockingQueue<>();
            }
        };
        private final DelegateThread thread = new DelegateThread();
        private boolean stopped;
        // Does not need to be volatile because only this thread can change it

        private final AtomicInteger count = new AtomicInteger();

        private InnerThread() {
            this.thread.start();
        }

        @Override
        public void addTask(Runnable task) {
            this.tasks.add(task);
            count.incrementAndGet();
        }

        @Override
        public void interrupt() {
            this.thread.interrupt();
            this.addTask(new Runnable() {
                @Override
                public void run() {
                    InnerThread.this.stopped = true;
                }
            });
        }

        @Override
        public Thread asThread() {
            return this.thread;
        }

        private class DelegateThread extends Thread {
            @Override
            public void run() {
                while (!InnerThread.this.stopped) {
                    try {
                        Runnable task = InnerThread.this.tasks.take();
                        task.run();
                    } catch (InterruptedException ignored) {
                        return;
                    }
                }
            }
        }

        public AtomicInteger getCount() {
            return this.count;
        }
    }
}
