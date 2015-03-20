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
import net.tridentsdk.concurrent.TaskExecutor;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.util.TridentLogger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.StampedLock;

/**
 * Thread list to allow task execution in a shared thread scaled with removal
 *
 * <p>Allows assignment of a worker to the user.</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class ConcurrentTaskExecutor extends AbstractExecutorService implements ExecutorFactory {
    private static final Set<ConcurrentTaskExecutor> EXECUTORS = Factories.collect().createSet();
    private static final int INITIALIZING = 0;
    private static final int STARTING = 1;
    private static final int RUNNING = 2;
    private static final int STOPPING = 3;
    private static final int TERMINATED = 4;

    static {       
        Thread thread = new Thread(() -> {
            while (true) {
                for (ConcurrentTaskExecutor ex : executors()) {
                    for (TaskExecutor e : ex.workerSet) {
                        Worker w = (Worker) e;
                        // TODO
                    }
                }

                try {
                    Thread.sleep(5 * 60 * 1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private final String name;

    private final List<TaskExecutor> workerSet = Lists.newCopyOnWriteArrayList();
    private final AtomicInteger count = new AtomicInteger();
    private final StampedLock lock = new StampedLock();
    private int scaleIdx = 0;

    private volatile int state = INITIALIZING;

    private ConcurrentTaskExecutor(int startingThreadCount, String name) {
        this.name = name;

        state = STARTING;
        for (int i = 0; i < startingThreadCount; i++) {
            addWorker(false);
        }
        state = RUNNING;
    }

    public static ConcurrentTaskExecutor create(int startingThreadCount, String name) {
        ConcurrentTaskExecutor ex = new ConcurrentTaskExecutor(startingThreadCount, name);
        EXECUTORS.add(ex);
        return ex;
    }

    @InternalUseOnly
    public static Collection<ConcurrentTaskExecutor> executors() {
        return EXECUTORS;
    }

    private Worker addWorker(boolean expire) {
        Worker worker;
        if (expire) {
            worker = new ExpiringWorker(count.getAndIncrement());
        } else {
            worker = new Worker(count.getAndIncrement());
        }

        workerSet.add(worker);
        worker.start();

        return worker;
    }

    @Override
    public TaskExecutor scaledThread() {
        long stamp = lock.readLock();
        int count;
        try {
            count = this.scaleIdx;
        } finally {
            lock.unlockRead(stamp);
        }

        if (count >= this.count.get()) {
            long stamp0 = lock.writeLock();
            try {
                this.scaleIdx = 0;
            } finally {
                lock.unlockWrite(stamp0);
            }

            return workerSet.get(0);
        } else {
            long stamp0 = lock.writeLock();
            try {
                this.scaleIdx++;
            } finally {
                lock.unlockWrite(stamp0);
            }
            return workerSet.get(count);
        }
    }

    @Override
    public List<TaskExecutor> threadList() {
        return workerSet;
    }

    @Override
    public void shutdown() {
        state = STOPPING;
        workerSet.forEach(TaskExecutor::interrupt);
        workerSet.clear();
        EXECUTORS.remove(this);
        state = TERMINATED;
    }

    // Executor implementations

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean isShutdown() {
        return state > STOPPING;
    }

    @Override
    public boolean isTerminated() {
        return state == TERMINATED;
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        long units = timeUnit.convert(System.nanoTime(), timeUnit);
        new Thread(this::shutdownNow).start();

        while (state != TERMINATED) {
            if (timeUnit.convert(System.nanoTime(), timeUnit) - units > l) {
                return false;
            }
        }

        return true;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        final RunnableFuture<T> future = new FutureTask<>(task);

        execute(future::run);
        return future;
    }

    @Override
    public void execute(@Nonnull Runnable runnable) {
        for (TaskExecutor ex : workerSet) {
            Worker w = (Worker) ex;
            if (w.tryAdd(runnable)) {
                return;
            }
        }

        Worker w = addWorker(true);
        w.addTask(runnable);
    }

    // Workers

    private class Worker extends Thread implements TaskExecutor {
        final ArrayDeque<Runnable> tasks = new ArrayDeque<>();
        final StampedLock lock = new StampedLock();

        public Worker(int index) {
            super("Pool " + name + " #" + index);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    nextTask().run();
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    TridentLogger.error(e);
                }
            }
        }

        private Runnable nextTask() throws InterruptedException {
            long stamp = lock.writeLockInterruptibly();
            try {
                Runnable runnable = tasks.poll();
                if (runnable == null) {
                    lock.tryUnlockWrite();
                    LockSupport.park();
                    return nextTask();
                } else {
                    return runnable;
                }
            } finally {
                if (stamp != 0L) {
                    lock.tryUnlockWrite();
                }
            }
        }

        boolean tryAdd(Runnable task) {
            long stamp = lock.tryWriteLock();
            if (stamp == 0L) {
                return false;
            } else {
                try {
                    tasks.offer(task);
                    LockSupport.unpark(this);
                    return true;
                } finally {
                    lock.tryUnlockWrite();
                }
            }
        }

        @Override
        public void addTask(Runnable task) {
            long stamp = lock.writeLock();
            try {
                tasks.offer(task);
                LockSupport.unpark(this);
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        @Override
        public <V> Future<V> submitTask(Callable<V> task) {
            final RunnableFuture<V> future = new FutureTask<>(task);

            addTask(future::run);
            return future;
        }

        @Override
        public void interrupt() {
            super.interrupt();
        }

        @Override
        public Thread asThread() {
            return this;
        }
    }

    private class ExpiringWorker extends net.tridentsdk.server.threads.ConcurrentTaskExecutor.Worker {
        public ExpiringWorker(int index) {
            super(index);
        }

        @Override
        public void addTask(Runnable task) {
            super.addTask(task);
            super.addTask(hintExit());
        }

        @Override
        boolean tryAdd(Runnable task) {
            boolean b = super.tryAdd(task);
            if (b) {
                super.addTask(hintExit());
            }

            return b;
        }

        private boolean isEmpty() {
            long stamp = lock.readLock();
            try {
                return tasks.isEmpty();
            } finally {
                lock.unlockRead(stamp);
            }
        }

        private Runnable hintExit() {
            return () -> {
                if (isEmpty()) {
                    super.interrupt();
                }
            };
        }
    }
}
