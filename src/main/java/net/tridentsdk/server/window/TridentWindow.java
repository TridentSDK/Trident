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

import net.tridentsdk.Handler;
import net.tridentsdk.base.Substance;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.entity.EntityBuilder;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.packets.play.out.PacketPlayOutCloseWindow;
import net.tridentsdk.server.packets.play.out.PacketPlayOutOpenWindow;
import net.tridentsdk.server.packets.play.out.PacketPlayOutSetSlot;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.WeakEntity;
import net.tridentsdk.window.Window;
import net.tridentsdk.window.inventory.Inventory;
import net.tridentsdk.window.inventory.InventoryType;
import net.tridentsdk.window.inventory.Item;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * An inventory window, wherever and whatever is holding it or having it open
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class TridentWindow implements Window, Inventory {
    /**
     * Counter for window ids, initial value is 2 to avoid confusion with a window and a player inventory
     */
    private static final AtomicInteger counter = new AtomicInteger(2);

    private final int id;
    private final String name;
    private final int length;
    private final InventoryType type;
    private final Set<WeakEntity<Player>> users = Factories.collect().createSet();
    private final AtomicReferenceArray<Item> contents;

    /**
     * Builds a new inventory window
     *
     * @param name   the title of the inventory
     * @param length the amount of slots in the inventory (should be multiple of 9)
     */
    private TridentWindow(String name, int length, InventoryType type) {
        this.name = name;
        this.length = length;
        this.id = counter.addAndGet(1);
        this.contents = new AtomicReferenceArray<>(length);
        for (int i = 0; i < contents.length(); i++) {
            contents.set(i, new Item(Substance.AIR));
        }

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

    public static TridentWindow create(String name, int length, InventoryType type) {
        TridentWindow window = new TridentWindow(name, length, type);
        Handler.forWindows().registerWindow(window);
        return window;
    }

    @Override
    public int windowId() {
        return this.id;
    }

    @Override
    public Item[] items() {
        Item[] array = new Item[contents.length()];
        for (int i = 0; i < contents.length(); i++) {
            array[i] = contents.get(i);
        }

        return array;
    }

    @Override
    public int length() {
        return this.length;
    }

    //@Override
    public int itemLength() {
        int counter = 0;
        for (Item item : items()) {
            if (item != null)
                counter++;
        }

        return counter;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Item itemAt(int slot) {
        return items()[slot];
    }

    @Override
    public void setSlot(int index, Item value) {
        contents.set(index, value);

        PacketPlayOutSetSlot setSlot = new PacketPlayOutSetSlot();
        setSlot.set("windowId", windowId()).set("slot", (short) index).set("item", new Slot(value));

        for (WeakEntity<Player> player : WeakEntity.iterate(users)) {
            ((TridentPlayer) player.obtain()).connection().sendPacket(setSlot);
        }
    }

    @Override
    public void putItem(Item item) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.compareAndSet(i, null, item)) {
                return;
            }
        }

        for (WeakEntity<Player> player : WeakEntity.iterate(users)) {
            // TODO implement
            TridentEntity dropped = EntityBuilder.create()
                    .spawn(player.obtain().position())
                    .build(TridentEntity.class);
            // TODO set dropped type
        }
    }

    public void sendTo(TridentPlayer player) {
        PacketPlayOutOpenWindow window = new PacketPlayOutOpenWindow();
        window.set("windowId", windowId())
                .set("inventoryType", type)
                .set("windowTitle", name())
                .set("slots", length())
                .set("entityId", -1);
        player.connection().sendPacket(window);

        for (int i = 0; i < length(); i++) {
            PacketPlayOutSetSlot setSlot = new PacketPlayOutSetSlot();
            setSlot.set("windowId", windowId()).set("slot", (short) i).set("item", new Slot(items()[i]));
            player.connection().sendPacket(window);
        }
        users.add(WeakEntity.<Player>of(player));
    }

    public void close(Player player, boolean force) {
        if (force) {
            ((TridentPlayer) player).connection().sendPacket(new PacketPlayOutCloseWindow().set("windowId", id));
        }

        users.remove(WeakEntity.finderOf(player));
    }
}
