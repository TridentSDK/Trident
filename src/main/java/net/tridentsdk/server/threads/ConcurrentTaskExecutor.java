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
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.StampedLock;

/**
 * Thread pool which allows tasks and result-bearing tasks to be executed concurrently
 *
 * <p>Internally, this class manages a List of the workers, which are simply TaskExecutors, and a global Set of other
 * executors. This allows all workers and executors in the server to be found easily. The worker List is an expandable </p>
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

    private final String name;

    private final List<TaskExecutor> workerSet = Lists.newCopyOnWriteArrayList();
    private final AtomicInteger count = new AtomicInteger();

    @GuardedBy("lock")
    private int scaleIdx = 0;
    private final StampedLock lock = new StampedLock();

    private volatile int state = INITIALIZING;

    private volatile long expireIntervalMillis = 60_000;
    private volatile boolean mustEmptyBeforeExpire = true;
    private volatile int maxScale = 500;

    @Override
    public int maxScale() {
        return maxScale;
    }

    @Override
    public void setMaxScale(int maxScale) {
        this.maxScale = maxScale;
    }

    @Override
    public long expireIntervalMillis() {
        return expireIntervalMillis;
    }

    @Override
    public void setExpireIntervalMillis(long expireIntervalMillis) {
        this.expireIntervalMillis = expireIntervalMillis;
    }

    @Override
    public boolean mustEmptyBeforeExpire() {
        return mustEmptyBeforeExpire;
    }

    @Override
    public void setMustEmptyBeforeExpire(boolean mustEmptyBeforeExpire) {
        this.mustEmptyBeforeExpire = mustEmptyBeforeExpire;
    }

    private ConcurrentTaskExecutor(int startingThreadCount, String name) {
        this.name = name;

        state = STARTING;
        count.set(startingThreadCount);
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
        if (count.get() < maxScale()) {
            if (expire) {
                worker = new ExpiringWorker(count.getAndIncrement());
            } else {
                worker = new Worker(count.getAndIncrement());
            }

            workerSet.add(worker);
            worker.start();
        } else {
            worker = (Worker) nextWorker();
        }

        return worker;
    }

    @Override
    public TaskExecutor nextWorker() {
        long stamp = lock.readLock();
        int count;
        try {
            count = this.scaleIdx;
        } finally {
            lock.unlockRead(stamp);
        }

        if (count >= this.count.get()) {
            long stamp0 = lock.tryConvertToWriteLock(stamp);
            if (stamp0 == 0L) {
                stamp0 = lock.writeLock();
            }
          
            try {
                this.scaleIdx = 0;
            } finally {
                lock.unlockWrite(stamp0);
            }

            return workerSet.get(0);
        } else {
            long stamp0 = lock.tryConvertToWriteLock(stamp);
            if (stamp0 == 0L) {
                stamp0 = lock.writeLock();
            }
          
            try {
                this.scaleIdx = 0;
            } finally {
                lock.unlockWrite(stamp0);
            }

            return workerSet.get(count);
        }
    }

    @Override
    public TaskExecutor scaledThread() {
        for (TaskExecutor ex : workerSet) {
            Worker w = (Worker) ex;
            if (!w.isHeld()) {
                return w;
            }
        }

        return addWorker(true);
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
            if (!w.isHeld()) {
                w.addTask(runnable);
                return;
            }
        }

        Worker w = addWorker(true);
        w.addTask(runnable);
    }

    // Workers

    private class Worker extends Thread implements TaskExecutor {
        @GuardedBy("lock")
        final ArrayDeque<Runnable> tasks = new ArrayDeque<>(64);
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

        Runnable nextTask() throws InterruptedException {
            long stamp = lock.writeLockInterruptibly();
            Runnable runnable = tasks.pollLast();
            if (runnable == null) {
                lock.unlockWrite(stamp);
                LockSupport.park();
                return nextTask();
            } else {
                lock.unlockWrite(stamp);
                return runnable;
            }
        }

        boolean isHeld() {
             return lock.isWriteLocked();
        }

        @Override
        public void addTask(Runnable task) {
            long stamp = lock.writeLock();
            try {
                tasks.offerFirst(task);
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

    private class ExpiringWorker extends Worker {
        long last = System.currentTimeMillis();

        public ExpiringWorker(int index) {
            super(index);
        }

        @Override
        Runnable nextTask() throws InterruptedException {
            long stamp = lock.readLockInterruptibly();
            Runnable runnable = tasks.peek();
            if (runnable == null) {
                // Non-reentrant, must release the lock
                lock.unlockRead(stamp);
                LockSupport.park();
                return nextTask();
            } else {
                lock.unlockRead(stamp);

                // Expiration mechanics
                long time = System.currentTimeMillis();
                if ((time - this.last) == expireIntervalMillis) {
                    if (mustEmptyBeforeExpire) {
                        if (isEmpty()) {
                            return () -> {
                                runnable.run();
                                this.interrupt();
                            };
                        }
                    }
                }

                this.last = time;
                return runnable;
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            workerSet.remove(this);
            count.decrementAndGet();
        }

        private boolean isEmpty() {
            long stamp = lock.readLock();
            try {
                return tasks.isEmpty();
            } finally {
                lock.unlockRead(stamp);
            }
        }
    }
}