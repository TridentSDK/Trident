/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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

/**
 * Managed set of threads that can be constrained
 * in CPU resources and performs work stealing when
 * necessary.
 */
public class ServerThreadPool {
    private static final int DOUBLING_THRESH = 8;

    private final Worker[] workers;

    public ServerThreadPool() {
        int cpus = Runtime.getRuntime().availableProcessors();
        if (cpus < DOUBLING_THRESH) {
            cpus <<= 1;

        }

        this.workers = new Worker[cpus];
    }

    public void execute(Runnable runnable) {

    }

    private class StealableDeque {
        private final Runnable[] array;

        public StealableDeque() {
            this.array = new Runnable[64];
        }

        public Runnable poll() {
            return null;
        }

        public Runnable pop() {
            return null;
        }

        public void push(Runnable runnable) {
        }
    }

    private class Worker extends Thread {
        private final StealableDeque deque = new StealableDeque();

        public Worker(int idx) {
            super("TRD - Worker - " + idx);
        }

        @Override
        public void run() {
            while (true) {
                Runnable runnable = this.deque.pop();
                if (runnable == null) {

                } else {
                    runnable.run();
                }
            }
        }
    }
}