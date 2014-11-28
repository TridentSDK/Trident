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
package net.tridentsdk.server.threads;

import com.google.common.collect.Lists;
import net.tridentsdk.api.docs.AccessNoDoc;
import net.tridentsdk.api.factory.ExecutorFactory;
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
 * @param <E> the assignment type, if used
 * @author The TridentSDK Team
 */
@ThreadSafe
public class ConcurrentTaskExecutor<E> extends AbstractExecutorService implements ExecutorFactory<E> {
    private static final int STARTING = 0;
    private static final int RUNNING = 1;
    private static final int SHUTTING_DOWN = 2;
    private static final int STOPPED = 3;

    // This is a final collection, initialization in the constructor is guaranteed to be visible if not changed
    // which it isn't
    private final List<TaskExecutor> executors = new ArrayList<>();

    // We cache assignments, if it is retrieved again while loading into the map, there would be 2 requests for the same
    // thing concurrently, which is bad for performance in the long run
    // It is better to have it slow now to cache correctly than time later to doubly receive
    private final ConcurrentCache<E, ThreadWorker> assigned = new ConcurrentCache<>();

    private volatile int state = STARTING;

    /**
     * Create a new executor using the number of threads to scale
     *
     * @param scale the threads to use
     */
    public ConcurrentTaskExecutor(int scale) {
        for (int i = 0; i < scale; i++)
            executors.add(new ThreadWorker().startWorker());

        state = RUNNING;
    }

    /**
     * Gets a thread that has the least amount of assignment uses. You must assign the user before this can scale.
     *
     * @return the thread with the lowest assignments
     */
    @Override
    public TaskExecutor scaledThread() {
        ThreadWorker lowest = null;
        for (TaskExecutor executor : executors) {
            ThreadWorker thread = (ThreadWorker) executor;
            if (lowest == null) lowest = thread;
            if (lowest.get() > thread.get()) lowest = thread;
        }

        return lowest;
    }

    /**
     * Assigns the scaled thread to the assignment
     *
     * <p>If already assigned, the executor is returned for the fast-path</p>
     *
     * @param assignment the assignment that uses the executor
     * @return the executor assigned
     */
    @Override
    public TaskExecutor assign(E assignment) {
        return assigned.retrieve(assignment, new Callable<ThreadWorker>() {
            @Override
            public ThreadWorker call() throws Exception {
                ThreadWorker worker = (ThreadWorker) scaledThread();
                worker.increment();
                return worker;
            }
        });
    }

    /**
     * Removes the assigned thread and reduces by one the scale factor for the thread
     *
     * @param assignment the assignment that uses the executor to be removed
     */
    @Override
    public void removeAssignment(E assignment) {
        ThreadWorker thread = this.assigned.remove(assignment);
        thread.decrement();
    }

    /**
     * Returns the assigned objects
     *
     * @return the assignments in the maps
     */
    @Override
    public Collection<E> values() {
        return this.assigned.keys();
    }

    /**
     * Lists all available task executors from the threads
     *
     * @return the thread list
     */
    @Override
    public List<TaskExecutor> threadList() {
        return this.executors;
    }

    /**
     * Shuts down the thread processes
     */
    @Override
    public void shutdown() {
        state = SHUTTING_DOWN;
        for (TaskExecutor thread : this.threadList())
            thread.interrupt();
        state = STOPPED;
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        return Lists.newArrayList();
    }

    @Override
    public boolean isShutdown() {
        return state == SHUTTING_DOWN;
    }

    @Override
    public boolean isTerminated() {
        return state == STOPPED;
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute(Runnable runnable) {
        scaledThread().addTask(runnable);
    }

    @AccessNoDoc
    private final class ThreadWorker extends Thread implements TaskExecutor {
        private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
        private final AtomicInteger integer = new AtomicInteger(0);

        private ThreadWorker() {
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

        public ThreadWorker startWorker() {
            super.start();
            return this;
        }

        @Override
        public void addTask(Runnable task) {
            tasks.add(task);
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    tasks.take().run();
                } catch (InterruptedException e) {
                    if (state == SHUTTING_DOWN)
                        break;
                    else {

                    }
                }
            }
        }

        @Override
        public Thread asThread() {
            return this;
        }
    }
}
