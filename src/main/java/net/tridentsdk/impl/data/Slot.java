/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.impl.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.tridentsdk.api.Material;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.api.nbt.*;

public class Slot implements Writable, NBTSerializable {

    @NBTField(name = "id", type = TagType.SHORT)
    private short id;
    private Material mat;

    @NBTField(name = "Slot", type = TagType.BYTE)
    private byte slot;
    @NBTField(name = "Count", type = TagType.BYTE)
    private volatile byte quantity;
    @NBTField(name = "Damage", type = TagType.SHORT)
    private volatile short damageValue;
    @NBTField(name = "tag", type = TagType.COMPOUND)
    private volatile CompoundTag compoundTag;

    public Slot(ByteBuf buf) {
        this.id = (short) buf.readByte();
        this.mat = Material.fromString(String.valueOf(this.id));

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

    public Slot(ItemStack is) {
        this.id = (short) is.getId();
        this.mat = is.getType();

        this.quantity = (byte) is.getQuantity();
        this.damageValue = (byte) is.getDamageValue();

        // TODO: build NBT data
    }

    protected Slot() {
    }

    /**
     * Gets the ID of the current item in the slot
     *
     * @return the item ID occupying the slot
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the type of the current item in the slot
     *
     * @return the item type occupying the slot
     */
    public Material getType() {
        return this.mat;
    }

    /**
     * Gets the amount of the current item in the slot
     *
     * @return the amount of the item occupying the slot
     */
    public short getQuantity() {
        return this.quantity;
    }

    /**
     * Gets the damage of the current item in the slot
     *
     * @return the damage of the item occupying the slot
     */
    public short getDamageValue() {
        return this.damageValue;
    }

    /**
     * Gets the NBT data of the current item in the slot
     *
     * @return the item NBT occupying the slot
     */
    public CompoundTag getCompoundTag() {
        return this.compoundTag;
    }

    public byte getSlot() {
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
            // TODO: write compound tag
        }
    }

    public ItemStack toItemStack() {
        ItemStack is = new ItemStack(Material.fromString(String.valueOf(id)));

        is.setQuantity(quantity);
        is.setDamageValue(damageValue);

        // TODO: transfer over item meta

        return is;
    }
}
