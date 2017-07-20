/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.tridentsdk.base.Substance;
import net.tridentsdk.inventory.Inventory;
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.server.net.Slot;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.packet.play.PlayOutOpenWindow;
import net.tridentsdk.server.packet.play.PlayOutSlot;
import net.tridentsdk.server.packet.play.PlayOutWindowItems;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.ui.chat.ChatComponent;
import net.tridentsdk.util.Tuple;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of an arbitrary inventory.
 */
@ThreadSafe
@RequiredArgsConstructor
public class TridentInventory implements Inventory {
    /**
     * The mapping of window IDs to their current viewers
     */
    private static final Map<Integer, Tuple<TridentInventory, Set<TridentPlayer>>> REGISTERED_WINDOWS =
            new ConcurrentHashMap<>();

    /**
     * The inventory ID
     */
    @Getter
    private final int id = calculateNextId();

    /**
     * Inventory type
     */
    @Getter
    private final InventoryType type;
    /**
     * The amount of slots available in this inventory
     */
    @Getter
    private final int size;
    /**
     * The title of this inventory
     */
    @Getter
    private volatile ChatComponent title = ChatComponent.text("Inventory");

    /**
     * The inventory contents
     */
    protected final Map<Integer, Item> contents = new ConcurrentHashMap<>();

    /**
     * Upon opening a window, this method will ensure that
     * the window is registered and retains a collection
     * of viewers that is removed if either the player
     * closes the window or leaves the server.
     *
     * @param inventory the inventory that that is being
     * opened
     * @param player the opening player
     */
    public static void open(TridentInventory inventory, TridentPlayer player) {
        // TODO horse
        REGISTERED_WINDOWS.compute(inventory.getId(), (k, v) -> {
            if (v == null) {
                Set<TridentPlayer> players = Collections.newSetFromMap(new WeakHashMap<>());
                players.add(player);
                return new Tuple<>(inventory, players);
            } else {
                v.getB().add(player);
                return v;
            }
        });

        player.net().sendPacket(new PlayOutOpenWindow(inventory, null));
        player.net().sendPacket(new PlayOutWindowItems(inventory));
    }

    /**
     * Closes the inventory with the given ID, deregistering
     * if no players are left having it opened.
     *
     * @param id the ID of the window to close
     * @param player the closing player
     */
    public static void close(int id, TridentPlayer player) {
        REGISTERED_WINDOWS.computeIfPresent(id, (k, v) -> {
            Set<TridentPlayer> b = v.getB();
            b.remove(player);

            return b.isEmpty() ? null : v;
        });
    }

    /**
     * Cleans out unused window IDs
     */
    public static void clean() {
        for (Integer key : REGISTERED_WINDOWS.keySet()) {
            REGISTERED_WINDOWS.computeIfPresent(key, (k, v) -> v.getB().isEmpty() ? null : v);
        }
    }

    /**
     * Calculates the next ID number for a new window.
     *
     * @return the next window ID
     */
    private static int calculateNextId() {
        int t;
        do {
            t = ThreadLocalRandom.current().nextInt(256);
        } while (REGISTERED_WINDOWS.containsKey(t));

        return t;
    }

    @Override
    public boolean add(Item item, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        Substance s = item.getSubstance();
        if (s == Substance.AIR) {
            throw new IllegalArgumentException("Cannot put AIR into inventory");
        }

        int maxStack = maxStack(s);

        for (Map.Entry<Integer, Item> entry : this.contents.entrySet()) {
            if (entry.getValue().getSubstance() == s) {
                int available = maxStack - entry.getValue().getCount();
                if (quantity > available) {
                    quantity -= available;
                    TridentItem value = new TridentItem(s, maxStack, item.getDamage(), item.getMeta());
                    this.sendViewers(new PlayOutSlot(this.id, entry.getKey(), Slot.newSlot(value)));
                    entry.setValue(value);
                } else {
                    TridentItem value = new TridentItem(s, quantity, item.getDamage(), item.getMeta());
                    this.sendViewers(new PlayOutSlot(this.id, entry.getKey(), Slot.newSlot(value)));
                    entry.setValue(value);
                    return true;
                }
            }
        }

        Item put = new TridentItem(s, Math.min(quantity, maxStack), item.getDamage(), item.getMeta());

        int slot = -1;
        for (Map.Entry<Integer, Item> entry : this.contents.entrySet()) {
            slot++;
            int key = entry.getKey();

            if (key > slot) {
                for (int i = slot; i < key; i++) {
                    Item result = this.contents.putIfAbsent(i, put);
                    if (result == null) {
                        this.sendViewers(new PlayOutSlot(this.id, i, Slot.newSlot(put)));
                        quantity -= put.getCount();
                        if (quantity > 0) {
                            put = new TridentItem(s, Math.min(quantity, maxStack), item.getDamage(), item.getMeta());
                        } else {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public Item add(int slot, Item item, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (slot < 0 || slot >= this.size) {
            throw new IllegalArgumentException("Illegal slot " + slot);
        }

        Substance sub = item.getSubstance();
        if (sub == Substance.AIR) {
            throw new IllegalArgumentException("Cannot put AIR into inventory");
        }

        Item computed = this.contents.compute(slot, (k, v) -> {
            if (v == null) {
                TridentItem tItem = new TridentItem(sub,
                        Math.min(maxStack(sub), item.getCount()),
                        item.getDamage(), item.getMeta());
                this.sendViewers(new PlayOutSlot(this.id, slot, Slot.newSlot(tItem)));
                return tItem;
            } else {
                if (v.getSubstance() == sub) {
                    TridentItem tItem = new TridentItem(sub,
                            Math.min(maxStack(sub), v.getCount() + item.getCount()),
                            item.getDamage(), item.getMeta());
                    this.sendViewers(new PlayOutSlot(this.id, slot, Slot.newSlot(tItem)));
                    return tItem;
                } else {
                    return v;
                }
            }
        });
        return computed.getSubstance() == sub ? null : computed;
    }

    @Nonnull
    @Override
    public Item remove(int slot, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (slot < 0 || slot >= this.size) {
            throw new IllegalArgumentException("Illegal slot " + slot);
        }

        Item computed = this.contents.compute(slot, (k, v) -> {
            if (v == null) {
                return null;
            } else {
                int left = Math.max(0, v.getCount() - quantity);
                if (left == 0) {
                    this.sendViewers(new PlayOutSlot(this.id, slot, Slot.EMPTY));
                    return null;
                } else {
                    TridentItem item = new TridentItem(v.getSubstance(), left, v.getDamage(), v.getMeta());
                    PacketOut packetOut = new PlayOutSlot(this.id, slot, Slot.newSlot(item));
                    this.sendViewers(packetOut);
                    return item;
                }
            }
        });
        return computed == null ? TridentItem.EMPTY : computed;
    }

    @Nonnull
    @Override
    public Item get(int slot) {
        if (slot < 0 || slot >= this.size) {
            throw new IllegalArgumentException("Illegal slot " + slot);
        }

        Item item = this.contents.get(slot);
        return item == null ? TridentItem.EMPTY : item;
    }

    @Override
    public void setTitle(ChatComponent title) {
        this.title = title;
    }

    /**
     * Sends all viewers of this inventory the given packet.
     *
     * @param packetOut the packet to send
     */
    protected void sendViewers(PacketOut packetOut) {
        Tuple<TridentInventory, Set<TridentPlayer>> tuple = REGISTERED_WINDOWS.get(this.id);
        if (tuple != null) {
            Set<TridentPlayer> players = tuple.getB();
            for (TridentPlayer player : players) {
                player.net().sendPacket(packetOut);
            }
        }
    }

    /**
     * Obtains the max stack size for an item of the given
     * Substance type.
     *
     * @param s the substance to obtain the max stack size
     * @return the substance's max stack size
     */
    protected static int maxStack(Substance s) {
        // TODO complete list
        if (s.hasDurability() || s == Substance.BOOK_AND_QUILL ||
                s == Substance.ENCHANTED_BOOK || s == Substance.LAVA_BUCKET) {
            return 1; // Tool, cannot be stacked
        } else if (s == Substance.SNOWBALL || s == Substance.BUCKET || s == Substance.EGG ||
                s == Substance.ENDER_PEARL || s == Substance.SIGN) {
            return  16;
        }

        return 64;
    }
}