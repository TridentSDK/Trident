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
package net.tridentsdk.server.net;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.tridentsdk.base.Substance;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.meta.nbt.NbtDecoder;
import net.tridentsdk.meta.nbt.TagCompound;

import javax.annotation.concurrent.Immutable;

/**
 * This class represents a protocol Slot object used to send
 * data pertaining to inventory items.
 */
@Immutable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Slot {
    /**
     * Represents a slot that contains no data
     */
    public static final Slot EMPTY = new Slot((short) -1, (byte) 0, (short) 0, null);

    // Self explanatory
    private final short id;
    private final byte count;
    private final short damage;
    private final TagCompound nbt;

    /**
     * Creates a new slot using the given information of a
     * server wrapper over an item.
     *
     * @param item the item which to fill the slot
     */
    private Slot(Item item) {
        this((short) item.getSubstance().getId(), (byte) item.getCount(), item.getDamage(), item.getMeta().toNbt());
    }

    /**
     * Creates a new slot using the given byte buffer to
     * read encoded values of the slot.
     *
     * @param buf the buffer which to read
     */
    public Slot(ByteBuf buf) {
        this(buf.readShort(), buf.readByte(), buf.readShort(), NbtDecoder.decode(buf));
    }

    /**
     * Creates a new slot using the information wrapped by
     * the given item.
     *
     * <p>This method automatically checks whether the item
     * is {@code null} and in which case, it returns
     * {@link #EMPTY}.</p>
     *
     * @param item the item which to send in slot format
     * @return the new slot
     */
    public static Slot newSlot(Item item) {
        if (item.getSubstance() == Substance.AIR) {
            return EMPTY;
        }

        return new Slot(item);
    }

    /**
     * Writes the slot data to the given byte buffer.
     *
     * @param buf the buffer which to write
     */
    public void write(ByteBuf buf) {
        buf.writeShort(this.id);

        if (this.id != -1) {
            buf.writeByte(this.count);
            buf.writeShort(this.damage);

            if (this.nbt != null) {
                this.nbt.write(buf);
            } else {
                buf.writeByte(0);
            }
        }
    }
}