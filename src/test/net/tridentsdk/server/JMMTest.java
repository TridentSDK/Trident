package net.tridentsdk.server;

import net.tridentsdk.window.inventory.Item;

public class JMMTest {
    private static volatile Item[] items = new Item[10];
    private static volatile Object object = new Object();

    public static void main(String[] args) throws InterruptedException {
        /*
         * Test visibility of volatile array and correctness
         *
         * null
         * net.tridentsdk.window.inventory.Item@112b1e53
         *
         *
        out.println(items[0]);
        Item[] items1 = items;
        items1[0] = new Item(Substance.ACACIA_DOOR);
        Item[] read = items;

        out.println(items[0]);
         */

        /************************************************/
        /*
         * Test Object visibility for volatile field set
         *
         * 605506535
         * 444916729
         *
         *
        final CountDownLatch latch = new CountDownLatch(1);
        out.println(object.hashCode());
        new Thread(new Runnable() {
            @Override
            public void run() {
                object = new Object();
                latch.countDown();
            }
        }).start();

        latch.await();
        out.println(object.hashCode());
         */
    }
}
