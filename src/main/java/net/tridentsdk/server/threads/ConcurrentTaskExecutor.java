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

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

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
 * realistic server load. However, because array based queues cannot resize, they are fixed at a default 20000000 tasks.
 * The resulting drawback is limited task size. In response, a second linked collection which is used for overflow tasks
 * and checked every other iteration of the task executor. However, due to the improved latency of task execution, the
 * likelihood of a task ever reaching this collection is very small under normal server load.</p>
 *
 * @param <E> the assignment type, if used
 * @author The TridentSDK Team
 */
@ThreadSafe public class ConcurrentTaskExecutor<E> extends AbstractExecutorService implements ExecutorFactory<E> {
    private static final Set<ConcurrentTaskExecutor<?>> EXECUTORS = Sets.newHashSet();

    private static final int EMERGENCY_MARGIN = 4;
    private static final boolean ARCH_64 = System.getProperty("os.arch").contains("64");
    private static final int TASK_LENGTH = calcTaskLen();

    private static final int STARTING = 0;
    private static final int RUNNING = 1;
    private static final int SHUTTING_DOWN = 2;
    private static final int STOPPED = 3;

    private volatile int state = STARTING;

    private final AtomicReferenceArray<ThreadWorker> executors;
    private final int scale;
    private final AtomicInteger emergencyScale = new AtomicInteger(1);

    private final Callable<ThreadWorker> obtainWorker = new Callable<ThreadWorker>() {
        @Override
        public ThreadWorker call() throws Exception {
            ThreadWorker worker = (ThreadWorker) scaledThread();
            return worker;
        }
    };

    // We cache assignments, if it is retrieved again while loading into the map, there would be 2 requests for the same
    // thing concurrently, which is bad for performance in the long run
    // It is better to have it slow now to cache correctly than time later to doubly receive
    private final ConcurrentCache<E, ThreadWorker> assigned = ConcurrentCache.create();

    private ConcurrentTaskExecutor(int scale) {
        this.scale = scale;
        executors = new AtomicReferenceArray<>(scale + EMERGENCY_MARGIN);

        for (int i = 0; i < scale; i++) {
            executors.set(i, new ThreadWorker(i).startWorker());
        }

        state = RUNNING;
    }

    private static int calcTaskLen() {
        int objectSize = 4;
        if (ARCH_64) objectSize = 8;
        long max = (Runtime.getRuntime().freeMemory() / objectSize) / 13; // TODO adjust thread count
        int len;
        if (max > (long) Integer.MAX_VALUE)
            len = Integer.MAX_VALUE - 8;
        else len = (int) max;

        return len;
    }

    /**
     * Create a new executor using the number of threads to scale
     *
     * @param scale the threads to use
     * @return a new concurrent task executor pool
     */
    public static <E> ConcurrentTaskExecutor<E> create(int scale) {
        ConcurrentTaskExecutor<E> executor = new ConcurrentTaskExecutor<>(scale);
        EXECUTORS.add(executor);
        return executor;
    }

    /**
     * Obtains a set of all the executors ever made in the instance of the server
     *
     * @return the set of created task executors
     */
    @InternalUseOnly
    public static Set<ConcurrentTaskExecutor<?>> executors() {
        return EXECUTORS;
    }

    private int counter = 0;

    @Override
    public TaskExecutor scaledThread() {
        synchronized (this) {
            if (counter == scale) {
                counter = 0;
            }

            return executors.get(counter++);
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
        for (int i = 0, n = scale; i < n; i++)
            execs.add(executors.get(i));
        return execs;
    }

    @Override
    public void shutdown() {
        state = SHUTTING_DOWN;
        for (int i = 0, n = scale; i < n; i++) {
            ThreadWorker thread = executors.get(i);
            if (thread == null) continue; // We want every single thread, including the overflow
            thread.interrupt();
            executors.set(i, null);
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
    public void execute(Runnable runnable) {
        ThreadWorker exec = (ThreadWorker) scaledThread();
        if (!exec.tasks.offer(runnable)) {
            // Overflow of tasks
            int threadIndex;

            // Check existing threads for space
            for (int i = 0; i < executors.length(); i++) {
                ThreadWorker worker = executors.get(i);
                if (worker == null) // Emergency thread, not set yet
                    continue;
                if (worker.tasks.size() < TASK_LENGTH) {
                    if (worker.tasks.offer(runnable))
                        return;
                    // Can't add to a worker, probably an overflow worker.
                    // Continue looking
                }
            }

            // If we reach here, no EXISTING thread has capacity to handle
            // Create a new overflow worker with an emergency index
            threadIndex = this.emergencyScale.incrementAndGet() + scale;
            if (!(threadIndex >= executors.length())) { // Make sure we have enough space for the extra thread
                handleShutdown(threadIndex, new ArrayBlockingQueue<>(TASK_LENGTH, false, Lists.newArrayList(runnable)));
                return;
            }

            // Use the overflow if necessary
            exec.addTask(runnable);
        }
    }

    public void handleShutdown(int index, Queue<Runnable> remaining) {
        if (state < SHUTTING_DOWN) {
            if (index > this.scale) {
                executors.set(index, new OverflowWorker(index).startWorker(remaining));
            } else executors.set(index, new ThreadWorker(index).startWorker(remaining));
        } else executors.set(index, null);

        remaining.clear();
    }

    // This class is designed to use an ArrayBlockingQueue
    // The normal Java executor uses a LinkedBlockingQueue
    // which fluctuates at 600-700 nanos on the test machine
    // using an ArrayBlockingQueue can speed up the insert
    // which fluctuates between 180-250 nanos, 3 times gain
    // However, using an ArrayBlockingQueue incurs significant
    // risk of task overloading and memory problems
    // To counter this, two queues are implemented, where tasks
    // are placed in the case the array queue is overloaded
    @AccessNoDoc private class ThreadWorker extends Thread implements TaskExecutor {
        protected final BlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(TASK_LENGTH);
        private final ConcurrentLinkedQueue<Runnable> overflow = new ConcurrentLinkedQueue<>();

        private final int index;

        private ThreadWorker(int index) {
            this.index = index;
        }

        public ThreadWorker startWorker() {
            super.start();
            return this;
        }

        public ThreadWorker startWorker(Queue<Runnable> tasks) {
            this.tasks.addAll(tasks);
            return startWorker();
        }

        // Remove overflow queue usage
        @Override
        public boolean addTask(Runnable task) {
            if (!tasks.offer(task)) {
                overflow.add(task);
                return false;
            }

            return true;
        }

        @Override
        public void run() {
            Thread.setDefaultUncaughtExceptionHandler(Defaults.EXCEPTION_HANDLER);

            while (!isInterrupted()) {
                try {
                    Runnable task = nextTask();

                    int cycles = 0;
                    while (task == null) {
                        task = nextTask();
                        Thread.yield();
                        if (cycles++ > 1024)
                            break;
                    }

                    if (task == null) {
                        Thread.yield();
                        task = tasks.take();
                    }

                    task.run();
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

        private Runnable nextTask() throws InterruptedException {
            Runnable task;
            if ((task = overflow.poll()) == null)
                task = tasks.poll();

            return task;
        }

        @Override
        public Thread asThread() {
            return this;
        }
    }

    private class OverflowWorker extends ThreadWorker {
        private OverflowWorker(int index) {
            super(index);
        }

        @Override
        public boolean addTask(Runnable task) {
            try {
                tasks.add(task);
            } catch (IllegalStateException e) {
                return false;
            }

            return true;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    Runnable task = tasks.take();
                    task.run();

                    int cycles = 0;
                    while (tasks.peek() == null) // Wait for new tasks
                        if (cycles++ == 1024) {  // No overflow tasks after 1024 cycles, exit
                            interrupt();
                            break;
                        }
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    TridentLogger.error(e); // Move the tasks back onto a normal thread, usually clear by then
                    handleShutdown(((ThreadWorker) scaledThread()).index, tasks);
                    return;
                }
            }

            // Exit the thread and clean up
            emergencyScale.decrementAndGet();
        }
    }
}