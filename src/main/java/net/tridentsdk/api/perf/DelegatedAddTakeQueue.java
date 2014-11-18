/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
