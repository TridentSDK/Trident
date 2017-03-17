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

import io.netty.buffer.ByteBuf;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.meta.nbt.ByteOutput;
import net.tridentsdk.meta.nbt.TagCompound;

import javax.annotation.concurrent.Immutable;

/**
 * This class represents a slot data type which is used by
 * the protocol to send and update items in inventories
 * (usually, not sure what else uses slots as well).
 */
// TODO
@Immutable
public class Slot {
    /**
     * A null instance of slot that contains no data
     */
    private static final Slot NULL = new Slot(-1);

    /**
     * The slot ID
     */
    private final int id;
    /**
     * The amount of the item
     */
    private final byte count;
    /**
     * The item damage value
     */
    private final int damage;
    /**
     * The NBT metadata on the item
     */
    private final TagCompound nbt;

    /**
     * Creates a new slot for the given item.
     *
     * @param item the item to create a new slot
     */
    public Slot(Item item) {
        this.id = item.getSubstance().getId();
        this.count = (byte) item.getCount();
        this.damage = item.getDamage();
        this.nbt = item.getMeta().toNbt();
    }

    /**
     * Creates a new slot with the given ID value.
     *
     * @param id the id value
     */
    private Slot(int id) {
        this.id = id;
        this.count = 1;
        this.damage = 0;
        this.nbt = null;
    }

    /**
     * Decodes a byte buffer.
     *
     * @param buf the buffer to decode
     * @return the slot from the buffer
     */
    public static Slot decode(ByteBuf buf) {
        // TODO
        return null;
    }

    /**
     * Obtains a singleton instance of an empty slot.
     *
     * @return an empty slot
     */
    public static Slot empty() {
        return NULL;
    }

    /**
     * Writes this slot data to the given buffer.
     *
     * @param buf the buffer which to write
     */
    public void write(ByteBuf buf) {
        buf.writeShort(this.id);

        if (this.id != -1) {
            buf.writeByte(this.count);
            buf.writeShort(this.damage);

            if (this.nbt != null) {
                this.nbt.write(new ByteOutput() {});
            } else {
                buf.writeByte(0);
            }
        }
    }
}