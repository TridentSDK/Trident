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

package net.tridentsdk.server.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.tridentsdk.base.Substance;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.window.inventory.Item;

public class Slot implements Writable, NBTSerializable {
    @NBTField(name = "id", type = TagType.SHORT)
    private short id;
    private Substance mat;

    @NBTField(name = "Slot", type = TagType.BYTE)
    protected byte slot;
    @NBTField(name = "Count", type = TagType.BYTE)
    protected volatile byte quantity;
    @NBTField(name = "Damage", type = TagType.SHORT)
    protected volatile short damageValue;
    @NBTField(name = "tag", type = TagType.COMPOUND)
    protected volatile CompoundTag compoundTag;

    public Slot(ByteBuf buf) {
        this.id = (short) buf.readByte();
        this.mat = Substance.fromId((byte) this.id);

        if (this.id == -1) {
            return;
        }

        this.quantity = buf.readByte();
        this.damageValue = buf.readShort();
        byte b;

        if ((b = buf.readByte()) != 0) {
            try {
                NBTDecoder builder = new NBTDecoder(new ByteBufInputStream(buf));

                this.compoundTag = builder.decode(b);
            } catch (NBTException ignored) {
                // do something
            }
        }
    }

    public Slot(Item is) {
        if (is == null)
            return;
        this.id = (short) is.id();
        this.mat = is.type();

        this.quantity = (byte) is.quantity();
        this.damageValue = (byte) is.damageValue();

        // TODO: build NBT data
    }

    protected Slot() {
    }

    /**
     * Gets the ID of the current item in the slot
     *
     * @return the item ID occupying the slot
     */
    public int id() {
        return this.id;
    }

    /**
     * Gets the type of the current item in the slot
     *
     * @return the item type occupying the slot
     */
    public Substance type() {
        return this.mat;
    }

    /**
     * Gets the amount of the current item in the slot
     *
     * @return the amount of the item occupying the slot
     */
    public short quantity() {
        return this.quantity;
    }

    /**
     * Gets the damage of the current item in the slot
     *
     * @return the damage of the item occupying the slot
     */
    public short damageValue() {
        return this.damageValue;
    }

    /**
     * Gets the NBT data of the current item in the slot
     *
     * @return the item NBT occupying the slot
     */
    public CompoundTag compoundTag() {
        return this.compoundTag;
    }

    public byte slot() {
        return slot;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.id);

        if (this.id == -1) {
            return;
        }

        buf.writeByte((int) this.quantity);
        buf.writeShort((int) this.damageValue);

        if (this.compoundTag != null) {
            // TODO: toPacket compound tag
        }
    }

    public Item item() {
        Item is = new Item(Substance.fromStringId(String.valueOf(id)));

        is.setQuantity(quantity);
        is.setDamageValue(damageValue);

        // TODO: transfer over item meta

        return is;
    }
}
