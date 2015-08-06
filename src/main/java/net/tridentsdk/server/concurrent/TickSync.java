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

import net.tridentsdk.server.TridentServer;
import net.tridentsdk.util.TridentLogger;

import javax.annotation.concurrent.GuardedBy;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;

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
 * <p>Unless you want to severely mess up the server, do not call any methods in this class unless:
 * <ul>
 *     <li>You know what you are doing (unlikely)</li>
 *     <li>Developing Trident (in which case you need to check with Pierre to ensure you're doing it right)</li>
 * </ul></p>
 *
 * @author The TridentSDK Team
 */
public final class TickSync {
    private TickSync() {
    }

    public static volatile boolean DEBUG = false;

    private static final Queue<String> expect = new ConcurrentLinkedQueue<>();
    private static final Queue<String> completed = new ConcurrentLinkedQueue<>();
    private static final LongAdder expected = new LongAdder();
    private static final LongAdder complete = new LongAdder();

    private static volatile CountDownLatch latch = new CountDownLatch(1);

    @GuardedBy("this")
    private static final Queue<Runnable> pluginTasks = new LinkedList<>();

    /**
     * Increments the expected updates counter
     */
    public static void increment(String s) {
        expected.increment();
        if (DEBUG) {
            expect.add(s + " T: " + Thread.currentThread().getName());
        }
    }

    /**
     * Records that an update has occurred
     * <p>
     * <p>Signals the main thread to sync method to continue if the expected and update counters match</p>
     */
    public static void complete(String s) {
        complete.increment();
        if (DEBUG) {
            completed.add(s + " T: " + Thread.currentThread().getName());
        }

        if (canProceed()) {
            latch.countDown();
        }
    }

    /**
     * Tests to see if the expected and update counters match
     *
     * @return {@code true} to indicate that the ticking can proceed
     */
    public static boolean canProceed() {
        return expected.sum() == complete.sum();
    }

    /**
     * Blocks the thread until this method is called again by a {@link #complete(String)} method
     */
    public static void awaitSync() {
        boolean b = canProceed();
        if (b) return;

        try {
            if (!latch.await(200, TimeUnit.MILLISECONDS)) {
                TridentLogger.get().warn("Lost tick sync: complete-" + complete.sum() + " needed-" + expected.sum() + " proceed-" + b);
                if (DEBUG) {
                    TridentLogger.get().warn("");
                    TridentLogger.get().warn("===== PRINTING COMPLETED TASKS =====");
                    completed.forEach(TridentLogger::warn);
                    TridentLogger.get().warn("===== END COMPLETED TASKS =====");
                    TridentLogger.get().warn("");
                    TridentLogger.get().warn("===== PRINTING NEEDED TASKS =====");
                    expect.forEach(TridentLogger::warn);
                    TridentLogger.get().warn("===== END NEEDED TASKS =====");
                    TridentLogger.get().warn("AVG TICK TIME: " + TridentServer.instance().mainThread().getAverageTickLength() + " ms");
                } else {
                    TridentLogger.get().warn("Enable debug to see extra information");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the counters and the blocking mechanisms for the next tick iteration
     */
    public static void reset() {
        expected.reset();
        complete.reset();
        expect.clear();
        completed.clear();
        latch = new CountDownLatch(1);
    }

    /**
     * Synchronizes the task for later execution once the tick completes
     *
     * @param pluginTask the task
     */
    public static void sync(Runnable pluginTask) {
        synchronized (TickSync.class) {
            pluginTasks.add(pluginTask);
        }

        if (canProceed()) {
            LockSupport.unpark(TridentServer.instance().mainThread());
        }
    }

    /**
     * Waits for a task to become available, or blocks until waitNanos has elapsed, in which case {@code null}
     * will be returned
     *
     * @param waitNanos the nanos to wait for a task
     * @return a task, or {@code null} if there were none
     * @throws InterruptedException if the current thread was interrupted in waiting for a task
     */
    public static Runnable waitForTask(long waitNanos) throws InterruptedException {
        synchronized (TickSync.class) {
            Runnable task;
            if ((task = pluginTasks.poll()) == null) {
                LockSupport.parkNanos(waitNanos);
                task = pluginTasks.poll();
            }

            return task;
        }
    }

    /**
     * Obtains the next task in the queue
     *
     * @return the next task, or {@code null} if there were none
     */
    public static Runnable next() {
        synchronized (TickSync.class) {
            return pluginTasks.poll();
        }
    }

    /**
     * Obtains the tasks left in the queue
     *
     * @return the amount of tasks left
     */
    public static int left() {
        synchronized (TickSync.class) {
            return pluginTasks.size();
        }
    }
}
