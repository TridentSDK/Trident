package net.tridentsdk.server.unit;

import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import net.tridentsdk.base.Substance;
import net.tridentsdk.window.inventory.Item;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class JMMTest extends AbstractTest {
    // volatileArray
    private volatile Item[] items = new Item[10];

    // volatileObject
    private final Object original = new Object();
    private volatile Object object = original;

    @Rule
    public ConcurrentRule concurrently = new ConcurrentRule();
    @Rule
    public RepeatingRule repeatedly = new RepeatingRule();

    private final AtomicReference<Item> reference = new AtomicReference<>();

    /**
     * Tests visibility of an object array
     */
    @Test
    @Concurrent(count = 16)
    @Repeating(repetition = 1000)
    public void volatileArray() {
        Item[] items1 = items;
        Item item =  new Item(Substance.ACACIA_DOOR);
        reference.set(item);
        items1[0] = item;
        Item[] read = items;
    }

    @After
    public void read() {
        assertEquals(items[0], reference.get());
    }

    /**
     * Tests the visibility of an object set to a volatile field
     *
     * @throws InterruptedException if the thread is interrupted when awaiting for the value
     */
    @Test
    @Repeating(repetition = 1000)
    public void volatileObject() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                object = new Object();
                latch.countDown();
            }
        }).start();

        latch.await();
        assertNotNull(object);
        assertNotEquals(object, original);
    }
}
