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

package net.tridentsdk.server.concurrent;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.tridentsdk.concurrent.SelectableThread;
import net.tridentsdk.concurrent.SelectableThreadPool;
import net.tridentsdk.docs.InternalUseOnly;

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
 * executors. This allows all workers and executors in the server to be found easily. The worker List is an expandable 
 * collection of internal thread workers. The decision to use a copy-on-write List instead of a Set was made based on
 * the need for index based access, as well as the majority of operations upon the collection iterations from thread
 * searching. Unfortunately, there are still many writes, as scaling requires the tracking of new workers, and the
 * removal of the workers that are no longer needed.</p>
 *
 * <p>This thread pool always maintains the starting concurrent. Scaling is done once the current workers are occupied at
 * the time of observation. Workers are deemed as occupied if concurrent are in the process of attempting insertion into
 * the worker's internal queue. Workers are managed by native park and unparking, rather than using conditions. This
 * provides numerous advantages, which include reduced overhead, as it is native, and is not bound to a particular scaleLock.
 * Additionally, native thread scheduling provides for more control over basic thread stopping, rather than using the
 * thread queue of a condition, or default guarding intrinsics.</p>
 * 
 * <p>There are two basic locking areas: first on the thread advancement counter, and in the worker itself. They are
 * both StampedLocks, which provide increased throughput (in fact, is the primary motivator for creating this class).
 * In place of this class can be instead, a ThreadPoolExecutor. However, many new concurrent updates in Java 8
 * rationalize an effort to create a new class which fully utilizes those features, and subsequently providing this
 * class which is optimized to execute the heterogeneous tasks provided by the server. The first scaleLock protects the
 * index which to pull workers from the worker Set, and a separate scaleLock, per-worker, protects the internal Deque. A
 * Deque was selected as it can be inserted from both ends, sizable, and is array-based. Tests confirm that array
 * based collections do outperform their node-based counter parts, as there is reduced instantiation overhead. The
 * explicitly declared scaleLock allows to check occupation of the worker, which increases scalability.</p>
 * 
 * <p>No thread pool would be complete without tuning. This class provides 3 basic tuning properties, which modify
 * <em>expiring concurrent</em>. Expiring concurrent are new concurrent are those created to scale the executor. They are
 * created when the current concurrent in the pool (including previously started expiring concurrent) are all occupied.
 * One may modify the time which the worker expires, whether the task queue must be empty, and the maximum amount
 * of concurrent in the pool.</p>
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class ConcurrentTaskExecutor extends AbstractExecutorService implements SelectableThreadPool {
    private static final Set<ConcurrentTaskExecutor> EXECUTORS = Sets.newConcurrentHashSet();
    private static final int INITIALIZING = 0;
    private static final int STARTING = 1;
    private static final int RUNNING = 2;
    private static final int STOPPING = 3;
    private static final int TERMINATED = 4;

    private final String name;

    private final List<SelectableThread> workerSet = Lists.newCopyOnWriteArrayList();
    private final AtomicInteger count = new AtomicInteger();
    private final int core;

    @GuardedBy("coreLock")
    private int coreIdx = 0;
    private final StampedLock coreLock = new StampedLock();

    @GuardedBy("scaleLock")
    private int scaleIdx = 0;
    private final StampedLock scaleLock = new StampedLock();

    private volatile int state = INITIALIZING;

    private volatile long expireIntervalMillis = 60_000;
    private volatile boolean mustEmptyBeforeExpire = true;
    private volatile int maxScale = 50;

    @Override
    public int maxThreads() {
        return maxScale;
    }

    @Override
    public void setMaxThreads(int maxScale) {
        this.maxScale = maxScale;
    }

    @Override
    public long threadExpiryTime() {
        return expireIntervalMillis;
    }

    @Override
    public void setThreadExpiryTime(long expireIntervalMillis) {
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
        this.core = startingThreadCount;

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

    private ConcurrentWorker addWorker(boolean expire) {
        ConcurrentWorker worker;
        if (count.get() < maxThreads()) {
            if (expire) {
                worker = new ExpiringWorker(count.getAndIncrement());
            } else {
                worker = new ConcurrentWorker(count.getAndIncrement());
            }

            workerSet.add(worker);
            worker.start();
        } else {
            worker = (ConcurrentWorker) selectNext();
        }

        return worker;
    }

    @Override
    public SelectableThread selectCore() {
        int count;
        int max = this.core - 1;

        long stamp = coreLock.readLock();
        try {
            count = this.coreIdx;
        } finally {
            coreLock.unlockRead(stamp);
        }

        if (count >= max) {
            count = 0;

            stamp = coreLock.writeLock();
            try {
                this.coreIdx = 0;
            } finally {
                coreLock.unlockWrite(stamp);
            }
        } else {
            stamp = coreLock.writeLock();
            try {
                coreIdx++;
            } finally {
                coreLock.unlockWrite(stamp);
            }
        }

        return workerSet.get(count);
    }

    @Override
    public SelectableThread selectNext() {
        int count;
        int max = this.workerSet.size();

        long stamp = scaleLock.readLock();
        try {
            count = this.scaleIdx;
        } finally {
            scaleLock.unlockRead(stamp);
        }

        if (count >= max) {
            count = 0;

            stamp = scaleLock.writeLock();
            try {
                this.scaleIdx = 0;
            } finally {
                scaleLock.unlockWrite(stamp);
            }
        } else {
            stamp = scaleLock.writeLock();
            try {
                scaleIdx++;
            } finally {
                scaleLock.unlockWrite(stamp);
            }
        }

        return workerSet.get(count);
    }

    @Override
    public SelectableThread selectScaled() {
        for (SelectableThread ex : workerSet) {
            ConcurrentWorker w = (ConcurrentWorker) ex;
            if (!w.isHeld()) {
                return w;
            }
        }

        return addWorker(true);
    }

    @Override
    public List<SelectableThread> workers() {
        return workerSet;
    }

    @Override
    public void shutdown() {
        state = STOPPING;
        workerSet.forEach(SelectableThread::interrupt);
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

    @Nonnull @Override
    public <T> Future<T> submit(Callable<T> task) {
        final RunnableFuture<T> future = new FutureTask<>(task);

        execute(future::run);
        return future;
    }

    @Override
    public void execute(@Nonnull Runnable runnable) {
        for (SelectableThread ex : workerSet) {
            ConcurrentWorker w = (ConcurrentWorker) ex;
            if (!w.isHeld()) {
                w.execute(runnable);
                return;
            }
        }

        ConcurrentWorker w = addWorker(true);
        w.execute(runnable);
    }

    // Workers

    private class ConcurrentWorker extends Thread implements SelectableThread {
        @GuardedBy("scaleLock")
        final Deque<Runnable> tasks = new ArrayDeque<>(64);
        final StampedLock lock = new StampedLock();

        volatile boolean held;

        public ConcurrentWorker(int index) {
            super("Pool " + name + " #" + index);
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    Runnable runnable = nextTask();
                    if (runnable == null) {
                        held = false;
                        LockSupport.park();
                    } else {
                        runnable.run();
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Runnable nextTask() throws InterruptedException {
            long stamp = lock.writeLockInterruptibly();
            try {
                return tasks.pollLast();
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        boolean isHeld() {
             return held;
        }

        @Override
        public void execute(Runnable task) {
            if (Thread.currentThread().equals(asThread())) {
                task.run();
            }

            long stamp = lock.writeLock();
            try {
                tasks.offerFirst(task);
            } finally {
                lock.unlockWrite(stamp);
            }

            held = true;
            LockSupport.unpark(this);
        }

        @Override
        public <V> Future<V> submitTask(Callable<V> task) {
            final RunnableFuture<V> future = new FutureTask<>(task);

            execute(future::run);
            return future;
        }

        @Override
        public void interrupt() {
            super.interrupt();

            long stamp = lock.writeLock();
            try {
                tasks.clear();
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        @Override
        public Thread asThread() {
            return this;
        }
    }

    private class ExpiringWorker extends ConcurrentWorker {
        long last = System.currentTimeMillis();

        public ExpiringWorker(int index) {
            super(index);
        }

        @Override
        Runnable nextTask() throws InterruptedException {
            long stamp = lock.readLockInterruptibly();
            Runnable runnable;
            try {
                runnable = tasks.pollLast();
            } finally {
                lock.unlockRead(stamp);
            }

            if (runnable == null) {
                // Expiration mechanics, in the case of spurious wakeups
                long time = System.currentTimeMillis();
                if ((time - this.last) >= expireIntervalMillis) {
                    this.interrupt();
                }

                // Processing tasks very very quickly can result in stackoverflows
                // if this method is called too often recursively
                return () -> LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(expireIntervalMillis));
            } else {
                // Expiration mechanics
                long time = System.currentTimeMillis();
                if ((time - this.last) >= expireIntervalMillis) {
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
            // Most important thing: don't allow new tasks to be submitted
            workerSet.remove(this);
            count.decrementAndGet();

            Queue<Runnable> left;
            long stamp = lock.readLock();
            try {
                left = tasks;
            } finally {
                lock.unlockRead(stamp);
            }

            // in case I dun goofed
            left.forEach(r -> selectNext().execute(r));

            super.interrupt();
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