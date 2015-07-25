package net.tridentsdk.server.concurrent;

import javax.annotation.concurrent.GuardedBy;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronizes plugin calls with the ticking thread
 *
 * <p>This class is necessary because plugins that are not following the Trident threading model can mutate and break
 * the state consistency as described by the model. This is not good because plugins should not be expected to follow
 * the threading model.</p>
 *
 * <p>A demonstration of the state consistency being broken by conflicting thread-models:
 * <pre>{@code
 *      WeatherConditions cnd = world.weather();
 *      if (!cnd.isRaining()) {    // If it is not raining
 *          cnd.setRaining(true);  // start raining
 *      }
 *
 *      // Should return true!
 *      boolean stillRaining = cnd.isRaining();
 * }</pre>
 * The {@code stillRaining} variable can return false:
 * <pre>
 *      World Tick Thread ---> raining == false -----------------> raining = !true -> raining == false
 *      Plugin Thread -----> raining == false -> raining = true -----------------------> stillRaining == false
 * </pre>
 * Not only do we not provide a set raining method, this class will be used to hold off plugin notification of server
 * events until the entire tick has been processed.
 * </p>
 *
 * @author The TridentSDK Team
 */
public final class TickSync {
    private TickSync() {
    }

    private static final LongAdder expected = new LongAdder();
    private static final LongAdder complete = new LongAdder();

    private static final CyclicBarrier proceed = new CyclicBarrier(1);

    @GuardedBy("taskLock")
    private static final Queue<Runnable> pluginTasks = new LinkedList<>();
    private static final Lock taskLock = new ReentrantLock();
    private static final Condition available = taskLock.newCondition();

    public static void increment() {
        expected.increment();
    }

    public static void complete() {
        complete.increment();

        if (canProceed()) {
            awaitSync();
        }
    }

    public static boolean canProceed() {
        return expected.sum() == complete.sum();
    }

    public static void awaitSync() {
        if (canProceed()) return;

        try {
            proceed.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        expected.reset();
        complete.reset();
        proceed.reset();
    }

    public static void sync(Runnable pluginTask) {
        taskLock.lock();
        try {
            pluginTasks.add(pluginTask);
            available.signal(); // SignalAll not needed, only the main thread waits on this condition
        } finally {
            taskLock.unlock();
        }
    }

    public static Runnable waitForTask(long waitNanos) throws InterruptedException {
        taskLock.lock();
        try {
            Runnable task;
            if ((task = pluginTasks.poll()) == null) {
                available.await(waitNanos, TimeUnit.NANOSECONDS);
            }

            return task;
        } finally {
            taskLock.unlock();
        }
    }

    public static Runnable next() {
        taskLock.lock();
        try {
            return pluginTasks.poll();
        } finally {
            taskLock.unlock();
        }
    }

    public static int left() {
        taskLock.lock();
        try {
            return pluginTasks.size();
        } finally {
            taskLock.unlock();
        }
    }
}
