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

import java.util.concurrent.BlockingQueue;

/**
 * Delegates insertions and takes from an AddTakeQueue to a given BlockingQueue
 *
 * <p>Adds are handled with {@link java.util.concurrent.BlockingQueue#offer(Object)}, and takes are handled with the
 * take method.</p>
 *
 * <p>Depending on the implementation (which may vary), the take method may or may not block or return a non-null
 * value.</p>
 *
 * @author The TridentSDK Team
 * @param <E> the element type in the queue
 * @see net.tridentsdk.api.perf.AddTakeQueue
 * @see java.util.concurrent.BlockingQueue
 */
public abstract class DelegatedAddTakeQueue<E> implements AddTakeQueue<E> {
    private final BlockingQueue<E> delegate = delegate();

    protected abstract BlockingQueue<E> delegate();

    @Override
    public boolean add(E e) {
        return delegate.offer(e);
    }

    @Override
    public E take() throws InterruptedException {
        return delegate.take();
    }
}
