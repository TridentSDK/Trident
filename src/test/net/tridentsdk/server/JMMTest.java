package net.tridentsdk.server;

import net.tridentsdk.base.Substance;
import net.tridentsdk.window.inventory.Item;

public class JMMTest {
    private static volatile Item[] items = new Item[10];

    public static void main(String[] args) {
        /*
         * null
         * net.tridentsdk.window.inventory.Item@112b1e53
         */
        System.out.println(items[0]);
        Item[] items1 = items;
        items1[0] = new Item(Substance.ACACIA_DOOR);
        Item[] read = items;

        System.out.println(items[0]);

        /************************************************/
    }
}
