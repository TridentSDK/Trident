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
