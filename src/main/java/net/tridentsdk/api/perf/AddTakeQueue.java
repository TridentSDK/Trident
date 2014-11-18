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
