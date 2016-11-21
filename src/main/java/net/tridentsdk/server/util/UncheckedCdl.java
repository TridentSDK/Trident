package net.tridentsdk.server.util;

import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch that does not throw a checked exception,
 * useful for preventing boilerplate in lambdas and places
 * where an exception should be rethrown by a
 * {@link RuntimeException}.
 */
public class UncheckedCdl extends CountDownLatch {
    public UncheckedCdl(int count) {
        super(count);
    }

    @Override
    public void await() {
        try {
            super.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}