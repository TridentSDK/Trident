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

import net.tridentsdk.api.factory.ExecutorFactory;
import net.tridentsdk.api.perf.AddTakeQueue;
import net.tridentsdk.api.perf.DelegatedAddTakeQueue;
import net.tridentsdk.api.threads.TaskExecutor;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread list to allow task execution in a shared thread scaled with removal
 *
 * <p>Allows assignment of a worker to the user</p>
 *
 * @param <Assignment> the assignment type, if used
 * @author The TridentSDK Team
 */
@ThreadSafe
public class ConcurrentTaskExecutor<Assignment> implements ExecutorFactory<Assignment> {
    // This is a final collection, initialization in the constructor is guaranteed to be visible if not changed
    // which it isn't
    private final List<TaskExecutor> executors = new ArrayList<>();

    // We cache assignments, if it is retrieved again while loading into the map, there would be 2 requests for the same
    // thing concurrently, which is bad for performance in the long run
    // It is better to have it slow now to cache correctly than time later to doubly receive
    private final ConcurrentCache<Assignment, InnerThread> assigned = new ConcurrentCache<>();
    private final ExecutorService executor;

    /**
     * Create a new executor using the number of threads to scale
     *
     * @param scale the threads to use
     */
    public ConcurrentTaskExecutor(int scale) {
        executor = Executors.newFixedThreadPool(scale);
        for (int i = 0; i < scale; i++) executors.add(new InnerThread());
    }

    /**
     * Gets a thread that has the least amount of assignment uses. You must assign the user before this can scale.
     *
     * @return the thread with the lowest assignments
     */
    public TaskExecutor scaledThread() {
        InnerThread lowest = null;
        for (TaskExecutor executor : executors) {
            InnerThread thread = (InnerThread) executor;
            if (lowest == null) lowest = thread;
            if (lowest.get() > thread.get()) lowest = thread;
        }

        return lowest;
    }

    /**
     * Assigns the scaled thread to the assignment
     * <p/>
     * <p>If already assigned, the executor is returned for the fast-path</p>
     *
     * @param assignment the assignment that uses the executor
     * @return the executor assigned
     */
    public TaskExecutor assign(Assignment assignment) {
        return assigned.retrieve(assignment, new Callable<InnerThread>() {
            @Override
            public InnerThread call() throws Exception {
                return (InnerThread) scaledThread();
            }
        }, executor);
    }

    /**
     * Removes the assigned thread and reduces by one the scale factor for the thread
     *
     * @param assignment the assignment that uses the executor to be removed
     */
    public void removeAssignment(Assignment assignment) {
        InnerThread thread = this.assigned.remove(assignment);
        thread.decrement();
    }

    /**
     * Returns the assigned objects
     *
     * @return the assignments in the maps
     */
    public Collection<Assignment> values() {
        return this.assigned.keys();
    }

    /**
     * Lists all available task executors from the threads
     *
     * @return the thread list
     */
    public List<TaskExecutor> threadList() {
        return this.executors;
    }

    /**
     * Shuts down the thread processes
     */
    public void shutdown() {
        for (TaskExecutor thread : this.threadList())
            thread.interrupt();
    }

    private final class InnerThread implements TaskExecutor {
        private final AddTakeQueue<Runnable> tasks = new DelegatedAddTakeQueue<Runnable>() {
            @Override protected BlockingQueue<Runnable> delegate() {
                return new LinkedBlockingQueue<>();
            }
        };

        private final DelegateThread thread = new DelegateThread();
        private final AtomicInteger integer = new AtomicInteger(0);

        private InnerThread() {
            this.thread.start();
        }

        public void increment() {
            integer.incrementAndGet();
        }

        public void decrement() {
            integer.decrementAndGet();
        }

        public int get() {
            return integer.get();
        }

        @Override
        public void addTask(Runnable task) {
            this.tasks.add(task);
        }

        @Override
        public void interrupt() {
            this.thread.interrupt();
        }

        @Override
        public Thread asThread() {
            return this.thread;
        }

        private class DelegateThread extends Thread {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Runnable task = InnerThread.this.tasks.take();
                        task.run();
                    } catch (InterruptedException ignored) {
                        return;
                    }
                }
            }
        }
    }
}
