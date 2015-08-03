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

package net.tridentsdk.server.entity;

import net.tridentsdk.base.Position;
import net.tridentsdk.entity.traits.WindowHolder;
import net.tridentsdk.inventory.Inventory;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.meta.nbt.ListTag;
import net.tridentsdk.meta.nbt.NBTSerializer;
import net.tridentsdk.meta.nbt.NBTTag;
import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityEquipment;
import net.tridentsdk.server.player.TridentPlayer;

import java.util.UUID;

/**
 * An entity that is able to hold an inventory
 *
 * @author The TridentSDK Team
 */
public abstract class TridentInventoryHolder extends TridentLivingEntity implements WindowHolder {
    protected volatile Inventory inventory;
    private volatile int selectedSlot = 0;

    /**
     * Inherits constructor from {@link TridentLivingEntity}
     */
    public TridentInventoryHolder(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public Inventory window() {
        return this.inventory;
    }

    @Override
    public Item heldItem() {
        return inventory.itemAt(TridentPlayer.SLOT_OFFSET + selectedSlot);
    }

    @Override
    public void setHeldItem(Item item) {
        inventory.setSlot(TridentPlayer.SLOT_OFFSET + selectedSlot, item);
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
        packet.set("entityId", entityId());
        packet.set("item", new Slot(item));
        packet.set("slot", (short) 0);

        TridentPlayer.sendFiltered(packet, (p) -> !p.equals(TridentInventoryHolder.this));
    }

    public void setSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    @Override
    public void load(CompoundTag tag) {
        if (this instanceof TridentPlayer) {
            return;
        }

        ListTag equipment = tag.getTagAs("Equipment");
        int index = 0;

        for (NBTTag t : equipment.listTags()) {
            inventory.setSlot(index++, NBTSerializer.deserialize(Slot.class,
                    t.asType(CompoundTag.class)).item());
        }
    }
}
