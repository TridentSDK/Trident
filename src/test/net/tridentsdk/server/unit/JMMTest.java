package net.tridentsdk.server.unit;

import net.tridentsdk.base.Substance;
import net.tridentsdk.window.inventory.Item;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class JMMTest {
    // volatileArray
    private volatile Item[] items = new Item[10];

    // volatileObject
    private final Object original = new Object();
    private volatile Object object = original;

    /**
     * Tests visibility of an object array
     */
    @Test
    public void volatileArray() {
        Item[] items1 = items;
        Item item =  new Item(Substance.ACACIA_DOOR);
        items1[0] = item;
        Item[] read = items;

        assertEquals(items[0], item);
    }

    /**
     * Tests the visibility of an object set to a volatile field
     *
     * @throws InterruptedException if the thread is interrupted when awaiting for the value
     */
    @Test
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
