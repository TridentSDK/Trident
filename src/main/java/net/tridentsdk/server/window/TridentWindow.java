/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.window;

import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.packets.play.out.PacketPlayOutOpenWindow;
import net.tridentsdk.server.packets.play.out.PacketPlayOutSetSlot;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.window.Window;
import net.tridentsdk.window.inventory.InventoryType;
import net.tridentsdk.window.inventory.Item;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An inventory window, wherever and whatever is holding it or having it open
 *
 * @author The TridentSDK Team
 */
public class TridentWindow implements Window {
    /**
     * Counter for window ids, initial value is 2 to avoid confusion with a window and a player inventory
     */
    private static final AtomicInteger counter = new AtomicInteger(2);

    private final int id;
    private final String name;
    private final int length;
    private final Item[] contents;
    private final InventoryType type;

    /**
     * Builds a new inventory window
     *
     * @param name   the title of the inventory
     * @param length the amount of slots in the inventory (should be multiple of 9)
     */
    public TridentWindow(String name, int length, InventoryType type) {
        this.name = name;
        this.length = length;
        this.id = counter.addAndGet(1);
        this.contents = new Item[length];
        this.type = type;
    }

    /**
     * Builds a new inventory window
     *
     * @param length the amount of slots in the inventory (should be multiple of 9)
     */
    public TridentWindow(int length) {
        this("", length, InventoryType.CHEST);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Item[] getItems() {
        return this.contents;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public int getItemLength() {
        int counter = 0;
        for (Item item : getItems())
            if (item != null) counter++;

        return counter;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setSlot(int index, Item value) {
        this.contents[index] = value;
        // TODO: update client
    }

    public void sendTo(TridentPlayer player) {
        PacketPlayOutOpenWindow window = new PacketPlayOutOpenWindow();
        window.set("windowId", getId())
                .set("inventoryType", type)
                .set("windowTitle", getName())
                .set("slots", getLength())
                .set("entityId", -1);
        player.getConnection().sendPacket(window);

        for (int i = 0; i < getLength(); i++) {
            PacketPlayOutSetSlot setSlot = new PacketPlayOutSetSlot();
            setSlot.set("windowId", getId())
                    .set("slot", (short) i)
                    .set("item", new Slot(getItems()[i]));
            player.getConnection().sendPacket(window);
        }
    }
}
