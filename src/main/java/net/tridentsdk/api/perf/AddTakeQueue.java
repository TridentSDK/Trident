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
