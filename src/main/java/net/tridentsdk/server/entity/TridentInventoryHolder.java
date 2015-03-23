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

import net.tridentsdk.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.docs.Volatile;
import net.tridentsdk.entity.traits.InventoryHolder;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.meta.nbt.ListTag;
import net.tridentsdk.meta.nbt.NBTSerializer;
import net.tridentsdk.meta.nbt.NBTTag;
import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.entity.living.TridentLivingEntity;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.window.inventory.Inventory;
import net.tridentsdk.window.inventory.Item;

import java.util.UUID;

/**
 * An entity that is able to hold an inventory
 *
 * @author The TridentSDK Team
 */
public abstract class TridentInventoryHolder extends TridentLivingEntity implements InventoryHolder {
    private final Object BARRIER;
    /**
     * The inventory held by the entity
     */
    @Volatile(policy = "Do not set after construction", reason = "Barrier", fix = "Set in constructor, do not change")
    protected Inventory inventory;

    /**
     * Inherits constructor from {@link net.tridentsdk.server.entity.living.TridentLivingEntity}
     */
    public TridentInventoryHolder(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
        BARRIER = new Object();
    }

    @Override
    public Inventory inventory() {
        return this.inventory;
    }

    @Override
    public Item heldItem() {
        // return inventory.items()[selectedSlot + 36]; TODO
        return new Item(Substance.AIR);
    }

    @Override
    public void setHeldItem(Item item) {
        // TODO
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
