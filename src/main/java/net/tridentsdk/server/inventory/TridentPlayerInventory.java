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
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.inventory.PlayerInventory;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.Slot;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.packet.play.PlayOutEquipment;
import net.tridentsdk.server.packet.play.PlayOutSlot;
import net.tridentsdk.server.player.RecipientSelector;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Implements an inventory that is held by the player
 */
@ThreadSafe
public class TridentPlayerInventory extends TridentInventory implements PlayerInventory {
    /**
     * The currently selected hotbar slot
     */
    @Getter
    private volatile int selectedSlot;
    /**
     * The player receiving the inventory
     */
    private final NetClient client;

    public TridentPlayerInventory(NetClient client) {
        super(InventoryType.PLAYER, 46);
        this.client = client;
    }

    @Override
    public boolean add(Item item, int quantity) {
        boolean add = super.add(item, quantity);
        this.update();
        return add;
    }

    @Override
    public Item add(int slot, Item item, int quantity) {
        Item add = super.add(slot, item, quantity);

        TridentPlayer player = this.client.getPlayer();
        if (slot == 5) {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 5, this.get(5)));
        }
        if (slot == 6) {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 4, this.get(6)));
        }
        if (slot == 7) {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 3, this.get(7)));
        }
        if (slot == 8) {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 2, this.get(8)));
        }
        if (slot == 45) {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 1, this.get(45)));
        }
        int heldSlot = this.selectedSlot;
        do {
            RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 0, this.get(36 + heldSlot)));
        } while (heldSlot != this.selectedSlot);

        return add;
    }

    @Nonnull
    @Override
    public Item getHeldItem() {
        return this.get(36 + this.selectedSlot);
    }

    @Nonnull
    @Override
    public Item getOffHeldItem() {
        return this.get(45);
    }

    @Override
    protected void sendViewers(PacketOut packetOut) {
        this.client.sendPacket(packetOut);
    }

    /**
     * Sets the player's selected slot, sending to those who
     * can view the player the player's held item.
     *
     * @param slot the new slot to set
     */
    public void setSelectedSlot(int slot) {
        this.selectedSlot = slot;

        TridentPlayer player = this.client.getPlayer();
        RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 0, this.getHeldItem()));
    }

    /**
     * Updates the player's inventory to the player who
     * holds this inventory.
     */
    public void update() {
        TridentPlayer player = this.client.getPlayer();
        this.contents.forEach((integer, item) -> {
            int i = integer.intValue();
            if (i == 5) {
                RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 5, item));
            }
            if (i == 6) {
                RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 4, item));
            }
            if (i == 7) {
                RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 3, item));
            }
            if (i == 8) {
                RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 2, item));
            }

            this.client.sendPacket(new PlayOutSlot(0, i, Slot.newSlot(item)));
        });

        RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 1, this.getOffHeldItem()));
        RecipientSelector.whoCanSee(player, true, new PlayOutEquipment(player, 0, this.getHeldItem()));
    }
}