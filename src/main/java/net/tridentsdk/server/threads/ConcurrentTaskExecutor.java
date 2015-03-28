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
 * executors. This allows all workers and executors in the server to be found easily. The worker List is an expandable 
 * collection of internal thread workers. The decision to use a copy-on-write List instead of a Set was made based on
 * the need for index based access, as well as the majority of operations upon the collection iterations from thread
 * searching. Unfortunately, there are still many writes, as scaling requires the tracking of new workers, and the
 * removal of the workers that are no longer needed.</p>
 * 
 * <p>This thread pool always maintains the starting threads. Scaling is done once the current workers are occupied at
 * the time of observation. Workers are deemed as occupied if threads are in the process of attempting insertion into
 * the worker's internal queue. Workers are managed by native park and unparking, rather than using conditions. This
 * provides numerous advantages, which include reduced overhead, as it is native, and is not bound to a particular lock.
 * Additionally, native thread scheduling provides for more control over basic thread stopping, rather than using the
 * thread queue of a condition, or default guarding intrinsics.</p>
 * 
 * <p>There are two basic locking areas: first on the thread advancement counter, and in the worker itself. They are
 * both StampedLocks, which provide increased throughput (in fact, is the primary motivator for creating this class).
 * In place of this class can be instead, a ThreadPoolExecutor. However, many new concurrent updates in Java 8
 * rationalize an effort to create a new class which fully utilizes those features, and subsequently providing this
 * class which is optimized to execute the heterogeneous tasks provided by the server. The first lock protects the
 * index which to pull workers from the worker Set, and a separate lock, per-worker, protects the internal Deque. A
 * Deque was selected as it can be inserted from both ends, sizable, and is array-based. Tests confirm that array
 * based collections do outperform their node-based counter parts, as there is reduced instantiation overhead. The
 * explicitly declared lock allows to check occupation of the worker, which increases scalability.</p>
 * 
 * <p>No thread pool would be complete without tuning. This class provides 3 basic tuning properties, which modify
 * <em>expiring threads</em>. Expiring threads are new threads are those created to scale the executor. They are
 * created when the current threads in the pool (including previously started expiring threads) are all occupied.
 * One may modify the time which the worker expires, whether the task queue must be empty, and the maximum amount
 * of threads in the pool.</p>
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

    public static void main(String[] args) throws InterruptedException {
        ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor(100, "Test");
        Thread.sleep(5000);

        for (int i = 0; i < 1000000; i++) {
            executor.scaledThread().addTask(() -> {
                for (int j = 0; j < 10000; j++) {
                    if (System.nanoTime() == new Object().hashCode()) {
                        break;
                    }
                }
            });
        }
    }

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
                this.scaleIdx++;
            } finally {
                lock.unlockWrite(stamp0);
            }

            return workerSet.get(count);
        }
    }

    @Override
    public TaskExecutor scaledThread() {
        /* for (TaskExecutor ex : workerSet) {
            Worker w = (Worker) ex;
            if (!w.isHeld()) {
                return w;
            }
        }

        return addWorker(true); */
        return nextWorker();
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
        /* for (TaskExecutor ex : workerSet) {
            Worker w = (Worker) ex;
            if (!w.isHeld()) {
                w.addTask(runnable);
                return;
            }
        }

        Worker w = addWorker(true);
        w.addTask(runnable); */
        nextWorker().addTask(runnable);
    }

    // Workers

    private class Worker extends Thread implements TaskExecutor {
        @GuardedBy("lock")
        final ArrayDeque<Runnable> tasks = new ArrayDeque<>(64);
        final StampedLock lock = new StampedLock();

        volatile boolean held;

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
                held = false;
                LockSupport.park();
                return nextTask();
            } else {
                lock.unlockWrite(stamp);
                return runnable;
            }
        }

        boolean isHeld() {
             return lock.isWriteLocked() && held;
        }

        @Override
        public void addTask(Runnable task) {
            long stamp = lock.writeLock();
            try {
                tasks.offerFirst(task);
                held = true;
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
            long stamp = lock.writeLockInterruptibly();
            Runnable runnable = tasks.pollLast();
            if (runnable == null) {
                // Non-reentrant, must release the lock
                lock.unlockWrite(stamp);
                
                // Expiration mechanics, in the case of spurious wakeups
                long time = System.currentTimeMillis();
                if ((time - this.last) == expireIntervalMillis) {
                    // Always empty, we just polled
                    this.interrupt();
                } else {
                    this.last = time;
                }
                
                LockSupport.park();
                return nextTask();
            } else {
                lock.unlockWrite(stamp);

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
