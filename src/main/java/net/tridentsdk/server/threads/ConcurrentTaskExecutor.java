/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server.threads;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Thread list to allow task execution in a shared thread scaled with removal <p/> <p>Allows assignment of a worker to
 * the user</p>
 *
 * @param <Assignment> the assignment type, if used
 * @author The TridentSDK Team
 */
public class ConcurrentTaskExecutor<Assignment> {
    private static final Map.Entry<?, ? extends Number> DEF_ENTRY = new AbstractMap.SimpleEntry<>(null, Long.MAX_VALUE);

    private final Map<InnerThread, Integer> scale = new HashMap<>();
    private final Map<Assignment, InnerThread> assignments = new HashMap<>();

    /**
     * Create a new executor using the number of threads to scale
     *
     * @param scale the threads to use
     */
    public ConcurrentTaskExecutor(int scale) {
        for (int i = 0; i < scale; i++) this.scale.put(new InnerThread(), 0);
    }

    private static <T> Map.Entry<T, ? extends Number> minMap(Map<T, ? extends Number> map) {
        Map.Entry<T, ? extends Number> ent = (Map.Entry<T, ? extends Number>) ConcurrentTaskExecutor.DEF_ENTRY;

        for (Map.Entry<T, ? extends Number> entry : map.entrySet())
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
        Map.Entry<InnerThread, ? extends Number> handler = ConcurrentTaskExecutor.minMap(this.scale);
        return handler.getKey();
    }

    /**
     * Assigns the scaled thread to the assignment <p/> <p>If already assigned, the executor is returned for the
     * fast-path</p>
     *
     * @param executor   the executor associated with the assignment
     * @param assignment the assignment that uses the executor
     * @return the executor assigned
     */
    public TaskExecutor assign(TaskExecutor executor, Assignment assignment) {
        if (!this.assignments.containsKey(assignment)) {
            Map.Entry<InnerThread, ? extends Number> handler = ConcurrentTaskExecutor.minMap(this.scale);
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

    public void shutdown() {
        for (InnerThread thread : this.scale.keySet())
            thread.interrupt();
        this.scale.clear();
        this.assignments.clear();
    }

    /**
     * Execution abstraction
     *
     * @author The TridentSDK Team
     */
    public interface TaskExecutor {
        /**
         * Adds the task to the queue
         *
         * @param task the task to add
         */
        void addTask(Runnable task);

        /**
         * Closes the thread and stops execution of new / remaining tasks
         */
        void interrupt();

        /**
         * Thread form
         *
         * @return the thread that is running
         */
        Thread asThread();
    }

    private static class InnerThread implements TaskExecutor {
        private final BlockingQueue<Runnable> tasks = new LinkedTransferQueue<>();
        private final DelegateThread thread = new DelegateThread();
        private boolean stopped;
        // Does not need to be volatile because only this thread can change it

        @Override
        public void addTask(Runnable task) {
            try {
                this.tasks.put(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                    }
                }
            }
        }
    }
}
