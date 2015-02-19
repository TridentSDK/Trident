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
import com.google.common.collect.Sets;
import net.tridentsdk.Defaults;
import net.tridentsdk.concurrent.ConcurrentCache;
import net.tridentsdk.concurrent.TaskExecutor;
import net.tridentsdk.docs.AccessNoDoc;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.util.TridentLogger;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Thread list to allow task execution in a shared thread scaled with removal
 *
 * <p>Allows assignment of a worker to the user.</p>
 *
 * <p>This is a concurrency baseline used in Trident. Anywhere there needs to be threads, the ConcurrentTaskExecutor
 * is the go-to class for thread pooling and task execution. This class serves the purpose to provide a fixed thread
 * pool whose function is optimized task execution concurrently, as the name suggests. The motivation behind extensive
 * testing and optimization of this class is to improve the existing executor framework to execute tasks with lower
 * submission latency, and the fact that the entire concurrency design falls upon this single class to keep up with the
 * demands of the server.</p>
 *
 * <p>Task metrics are performed in each worker in order to balance task execution between the threads in the pool.
 * Thread confinement and consistency can be achieved by acquiring a single internal worker thread, which provides the
 * necessary methods and external handling within the pool to allow the request to keep executing tasks on the worker
 * while still having the same balancing scalability provided by the distributed {@link #execute(Runnable)} method.</p>
 *
 * <p>A different approach is used to task execution by the workers. Instead of using a linked concurrent collection,
 * which increases the latency between submission and execution phases of the task, the worker stores tasks queued to
 * execute using an {@link java.util.concurrent.ArrayBlockingQueue}. This suppresses GC overhead and latency by direct
 * array store instead of node linking. Albeit fast, linked collections do not out perform an array based queue under
 * realistic server load. However, because array based queues cannot resize, they are fixed at a default 20000000
 * tasks.
 * The resulting drawback is limited task size. In response, a second linked collection which is used for overflow
 * tasks
 * and checked every other iteration of the task executor. However, due to the improved latency of task execution, the
 * likelihood of a task ever reaching this collection is very small under normal server load.</p>
 *
 * @param <E> the assignment type, if used
 * @author The TridentSDK Team
 */
@ThreadSafe
public class ConcurrentTaskExecutor<E> extends AbstractExecutorService implements ExecutorFactory<E> {
    private static final Set<ConcurrentTaskExecutor<?>> EXECUTORS = Sets.newHashSet();

    private static final int STARTING = 0;
    private static final int RUNNING = 1;
    private static final int SHUTTING_DOWN = 2;
    private static final int STOPPED = 3;

    private final ThreadWorker[] workers;

    private final int scale;
    private final Callable<ThreadWorker> obtainWorker = new Callable<ThreadWorker>() {
        @Override
        public ThreadWorker call() throws Exception {
            return (ThreadWorker) scaledThread();
        }
    };
    // We cache assignments, if it is retrieved again while loading into the map, there would be 2 requests for the same
    // thing concurrently, which is bad for performance in the long run
    // It is better to have it slow now to cache correctly than time later to doubly receive
    private final ConcurrentCache<E, ThreadWorker> assigned = ConcurrentCache.create();
    private volatile int state = STARTING;

    @GuardedBy("this")
    private int counter = 0;

    private ConcurrentTaskExecutor(int scale, String name) {
        this.scale = scale;

        this.workers = (ThreadWorker[]) Array.newInstance(ThreadWorker.class, scale);
        for (int i = 0; i < scale; i++) {
            workers[i] = new ThreadWorker(i, name).startWorker();
        }

        state = RUNNING;
    }

    /**
     * Create a new executor using the number of threads to scale
     *
     * @param scale the threads to use
     * @return a new concurrent task executor pool
     */
    public static <E> ConcurrentTaskExecutor<E> create(int scale, String name) {
        ConcurrentTaskExecutor<E> executor = new ConcurrentTaskExecutor<>(scale, name);
        EXECUTORS.add(executor);
        return executor;
    }

    /**
     * Obtains a set of all the workers ever made in the instance of the server
     *
     * @return the set of created task workers
     */
    @InternalUseOnly
    public static Set<ConcurrentTaskExecutor<?>> executors() {
        return EXECUTORS;
    }

    @Override
    public TaskExecutor scaledThread() {
        synchronized (this) {
            if (counter == scale) {
                counter = -1;
            }

            return workers[counter++];
        }
    }

    @Override
    public TaskExecutor assign(E assignment) {
        return assigned.retrieve(assignment, obtainWorker);
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
        this.assigned.remove(assignment);
    }

    @Override
    public Collection<E> values() {
        return this.assigned.keys();
    }

    @Override
    public List<TaskExecutor> threadList() {
        List<TaskExecutor> execs = Lists.newArrayList();
        for (ThreadWorker worker : workers)
            execs.add(worker);
        return execs;
    }

    @Override
    public void shutdown() {
        state = SHUTTING_DOWN;
        for (ThreadWorker worker : workers) {
            worker.interrupt();
        }

        assigned.clear();
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
    public <T> Future<T> submit(Callable<T> task) {
        final RunnableFuture<T> future = new FutureTask<>(task);
        execute(new Runnable() {
            @Override
            public void run() {
                future.run();
            }
        });
        return future;
    }

    @Override
    public void execute(Runnable runnable) {
        scaledThread().addTask(runnable);
    }

    @AccessNoDoc
    private class ThreadWorker extends Thread implements TaskExecutor {
        private final BlockingQueue<Runnable> tasks = new LinkedBlockingDeque<>();

        private ThreadWorker(int index, String name) {
            // This is only safe because it is constructed in the CTE factory, otherwise the size
            // may change throughout the threads as the workers expand
            // tip - don't try this at home!
            super("Trident - CTE " + EXECUTORS.size() + " Thread " + index + " - " + name);
        }

        public ThreadWorker startWorker() {
            super.start();
            return this;
        }

        @Override
        public void interrupt() {
            tasks.clear();
            super.interrupt();
        }

        @Override
        public void addTask(Runnable task) {
            tasks.offer(task);
        }

        @Override
        public <V> Future<V> submitTask(Callable<V> task) {
            final RunnableFuture<V> future = new FutureTask<>(task);
            addTask(new Runnable() { // Be VERY careful -- This is addTask, NOT execute
                @Override
                public void run() {
                    future.run();
                }
            });
            return future;
        }

        @Override
        public void run() {
            Thread.setDefaultUncaughtExceptionHandler(Defaults.EXCEPTION_HANDLER);

            while (!isInterrupted()) {
                try {
                    nextTask().run();
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Must hold lock
        private Runnable nextTask() throws InterruptedException {
            Runnable runnable = tasks.poll(60, TimeUnit.NANOSECONDS);
            if (runnable == null) {
                return tasks.take();
            }

            return runnable;
        }

        @Override
        public Thread asThread() {
            return this;
        }
    }
}
