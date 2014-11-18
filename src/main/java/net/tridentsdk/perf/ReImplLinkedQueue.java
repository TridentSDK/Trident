package net.tridentsdk.perf;

import net.tridentsdk.api.perf.*;
import net.tridentsdk.api.perf.Performance;
import sun.misc.Unsafe;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReImplLinkedQueue<E> implements net.tridentsdk.api.perf.AddTakeQueue<E> {
    private static final Unsafe UNSAFE = net.tridentsdk.api.perf.Performance.getUnsafe();

    // DO NOT FINALIZE THESE
    private volatile Node<E> head = new Node<>(null, null);
    private volatile Node<E> tail = head;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    @Override
    public boolean add(E e) {
        Node<E> n = new Node<>(e, null);
        for (;;) {
            Node<E> t = tail;
            Node<E> s = t.getNext();
            if (t == tail) {
                if (s == null) {
                    if (t.casNext(null, n)) {
                        casTail(t, n);
                        lock.lock();
                        try {
                            condition.signalAll();
                            return true;
                        } finally {
                            lock.unlock();
                        }
                    }
                } else {
                    casTail(t, s);
                }
            }
        }
    }

    @Override
    public E take() throws InterruptedException {
        while (head == tail) {
            lock.lock();
            try {
                condition.await();
            } finally {
                lock.unlock();
            }
        }

        for (;;) {
            Node<E> h = head;
            Node<E> t = tail;
            Node<E> first = h.getNext();

            if (h == head) {
                if (h == t) {
                    casTail(t, first);
                } else if (casHead(h, first)) {
                    E item = first.getItem();
                    if (item != null) {
                        first.setItem(null);
                        return item;
                    }
                }
            }
        }
    }

    private static final long HEAD = net.tridentsdk.api.perf.Performance.wrap("head").address();
    public boolean casHead(Node<E> old, Node<E> node) {
        return UNSAFE.compareAndSwapObject(this, HEAD, old, node);
    }

    private static final long TAIL = net.tridentsdk.api.perf.Performance.wrap("tail").address();
    public boolean casTail(Node<E> old, Node<E> node) {
        return UNSAFE.compareAndSwapObject(this, TAIL, old, node);
    }

    private static class Node<E> {
        // DO NOT FINALIZE THESE
        private volatile E item;
        private volatile Node<E> next;

        public Node(E item, Node<E> next) {
            this.item = item;
            this.next = next;
        }

        private static final long ITEM = net.tridentsdk.api.perf.Performance.wrap("item").address();
        public void setItem(E item) {
            UNSAFE.putObjectVolatile(this, ITEM, item);
        }

        public E getItem() {
            return item;
        }

        private static final long NEXT = Performance.wrap("next").address();
        public boolean casNext(Node<E> old, Node<E> node) {
            return UNSAFE.compareAndSwapObject(this, NEXT, old, node);
        }

        public Node<E> getNext() {
            return next;
        }
    }
}
