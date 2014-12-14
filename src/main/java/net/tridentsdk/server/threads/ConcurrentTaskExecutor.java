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
import net.tridentsdk.Defaults;
import net.tridentsdk.concurrent.ConcurrentCache;
import net.tridentsdk.concurrent.TaskExecutor;
import net.tridentsdk.docs.AccessNoDoc;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.perf.Performance;
import net.tridentsdk.util.TridentLogger;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

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

    private final AtomicReferenceArray<ThreadWorker> executors;

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
        executors = new AtomicReferenceArray<>(scale);

        for (int i = 0; i < scale; i++)
            executors.set(i, new ThreadWorker(i).startWorker());

        state = RUNNING;
    }

    @Override
    public TaskExecutor scaledThread() {
        ThreadWorker lowest = null;
        for (int i = 0, n = executors.length(); i < n; i++) {
            ThreadWorker thread = executors.get(i);
            if (lowest == null) lowest = thread;
            if (lowest.get() > thread.get()) lowest = thread;
        }

        return lowest;
    }

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

    @Override
    public void set(final TaskExecutor executor, E assignment) {
        assigned.retrieve(assignment, new Callable<ThreadWorker>() {
            @Override
            public ThreadWorker call() throws Exception {
                return (ThreadWorker) executor;
            }
        });
    }

    @Override
    public void removeAssignment(E assignment) {
        ThreadWorker thread = this.assigned.remove(assignment);
        thread.decrement();
    }

    @Override
    public Collection<E> values() {
        return this.assigned.keys();
    }

    @Override
    public List<TaskExecutor> threadList() {
        List<TaskExecutor> execs = new ArrayList<>();
        for (int i = 0, n = executors.length(); i < n; i++)
            execs.add(executors.get(i));
        return execs;
    }

    @Override
    public void shutdown() {
        state = SHUTTING_DOWN;
        for (TaskExecutor thread : this.threadList())
            thread.interrupt();
        for (E e : assigned.keys()) {
            ThreadWorker worker = assigned.remove(e);

            Performance.getUnsafe().unpark(worker);
            worker.interrupt(); // Just in case
        }

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
        TridentLogger.error(new UnsupportedOperationException());
        return false;
    }

    @Override
    public void execute(Runnable runnable) {
        scaledThread().addTask(runnable);
    }

    public void handleShutdown(int index, Queue<Runnable> remaining) {
        if (state < SHUTTING_DOWN)
            executors.set(index, new ThreadWorker(index).startWorker(remaining));
        else executors.set(index, null);
        remaining.clear();
    }

    @AccessNoDoc
    private final class ThreadWorker extends Thread implements TaskExecutor {
        private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
        private final AtomicInteger integer = new AtomicInteger(0);

        private final int index;

        private ThreadWorker(int index) {
            this.index = index;
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

        public ThreadWorker startWorker(Queue<Runnable> tasks) {
            this.tasks.addAll(tasks);
            return startWorker();
        }

        @Override
        public void addTask(Runnable task) {
            tasks.add(task);
        }

        @Override
        public void run() {
            Thread.setDefaultUncaughtExceptionHandler(Defaults.EXCEPTION_HANDLER);

            while (!isInterrupted()) {
                try {
                    tasks.take().run();
                } catch (InterruptedException e) {
                    handleShutdown(index, tasks);
                    return;
                } catch (Exception e) {
                    TridentLogger.error(e);
                    handleShutdown(index, tasks);
                    return;
                }
            }
        }

        @Override
        public Thread asThread() {
            return this;
        }
    }
}
