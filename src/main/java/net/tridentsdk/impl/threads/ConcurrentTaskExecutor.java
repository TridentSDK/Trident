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
package net.tridentsdk.impl.threads;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import net.tridentsdk.api.perf.AddTakeQueue;
import net.tridentsdk.api.perf.DelegatedAddTakeQueue;
import net.tridentsdk.api.threads.TaskExecutor;

import javax.annotation.Nullable;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread list to allow task execution in a shared thread scaled with removal
 *
 * <p>Allows assignment of a worker to the user</p>
 *
 * @param <Assignment> the assignment type, if used
 * @author The TridentSDK Team
 */
public class ConcurrentTaskExecutor<Assignment> {
    private static final Entry<?, ? extends Number> DEF_ENTRY = new SimpleEntry<>(null, Long.MAX_VALUE);

    private final Map<InnerThread, Integer> scale = new HashMap<>();
    private final Map<Assignment, InnerThread> assignments = new HashMap<>();

    private final List<TaskExecutor> executors;

    /**
     * Create a new executor using the number of threads to scale
     *
     * @param scale the threads to use
     */
    public ConcurrentTaskExecutor(int scale) {
        for (int i = 0; i < scale; i++) this.scale.put(new InnerThread(), 0);
        this.executors = Lists.newArrayList(Iterators.transform(this.scale.keySet().iterator(), new Function
                <InnerThread, TaskExecutor>() {
            @Nullable
            @Override
            public TaskExecutor apply(@Nullable InnerThread innerThread) {
                return innerThread;
            }
        }));
    }

    private static <T> Entry<T, ? extends Number> minMap(Map<T, ? extends Number> map) {
        Entry<T, ? extends Number> ent = (Entry<T, ? extends Number>) DEF_ENTRY;

        for (Entry<T, ? extends Number> entry : map.entrySet())
            if (entry.getValue().longValue() < ent.getValue().longValue())
                ent = entry;

        return ent;
    }

    /**
     * Gets a thread that has the least amount of assignment uses. You must assign the user before this can scale.
     *
     * @return the thread with the lowest assignments
     */
    public TaskExecutor getScaledThread() {
        Entry<InnerThread, ? extends Number> handler = minMap(this.scale);
        return handler.getKey();
    }

    /**
     * Assigns the scaled thread to the assignment
     * <p/>
     * <p>If already assigned, the executor is returned for the fast-path</p>
     *
     * @param executor   the executor associated with the assignment
     * @param assignment the assignment that uses the executor
     * @return the executor assigned
     */
    public TaskExecutor assign(TaskExecutor executor, Assignment assignment) {
        if (!this.assignments.containsKey(assignment)) {
            Entry<InnerThread, ? extends Number> handler = minMap(this.scale);
            InnerThread thread = handler.getKey();

            this.assignments.put(assignment, thread);
            this.scale.put(handler.getKey(), Integer.valueOf(handler.getValue().intValue() + 1));

            return thread;
        }

        return executor;
    }

    /**
     * Removes the assigned thread and reduces by one the scale factor for the thread
     *
     * @param assignment the assignment that uses the executor to be removed
     */
    public void removeAssignment(Assignment assignment) {
        InnerThread thread = this.assignments.remove(assignment);
        if (thread != null) this.scale.put(thread, this.scale.get(thread) + 1);
    }

    /**
     * Returns the assigned objects
     *
     * @return the assignments in the maps
     */
    public Collection<Assignment> values() {
        return this.assignments.keySet();
    }

    /**
     * Lists all available task executors from the threads
     *
     * @return the thread list
     */
    public List<TaskExecutor> threadList() {
        return this.executors;
    }

    /**
     * Shuts down the thread processes
     */
    public void shutdown() {
        for (TaskExecutor thread : this.scale.keySet())
            thread.interrupt();
        this.scale.clear();
        this.assignments.clear();
    }

    private static final class InnerThread implements TaskExecutor {
        private final AddTakeQueue<Runnable> tasks = new DelegatedAddTakeQueue<Runnable>() {
            @Override protected BlockingQueue<Runnable> delegate() {
                return new LinkedBlockingQueue<>();
            }
        };
        private final DelegateThread thread = new DelegateThread();
        private boolean stopped;
        // Does not need to be volatile because only this thread can change it

        private InnerThread() {
            this.thread.start();
        }

        @Override
        public void addTask(Runnable task) {
            this.tasks.add(task);
        }

        @Override
        public void interrupt() {
            this.thread.interrupt();
            this.addTask(new Runnable() {
                @Override
                public void run() {
                    InnerThread.this.stopped = true;
                }
            });
        }

        @Override
        public Thread asThread() {
            return this.thread;
        }

        private class DelegateThread extends Thread {
            @Override
            public void run() {
                while (!InnerThread.this.stopped) {
                    try {
                        Runnable task = InnerThread.this.tasks.take();
                        task.run();
                    } catch (InterruptedException ignored) {
                        return;
                    }
                }
            }
        }
    }
}
