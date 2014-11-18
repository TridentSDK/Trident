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
package net.tridentsdk.api.perf;

/**
 * A queue that only supports safe insertion and blocking removals
 *
 * @param <E> the element type in the queue
 */
public interface AddTakeQueue<E> {
    /**
     * Inserts an element into the queue
     *
     * @param e the element to insert
     * @return {@code true} if the operation completes successfully
     */
    boolean add(E e);

    /**
     * Takes an element out of the queue, blocking if necessary to wait for the queue to become non-empty
     *
     * @return the element at the tail of the queue
     * @throws InterruptedException if the operation is blocked when the thread is interrupted
     */
    E take() throws InterruptedException;
}
