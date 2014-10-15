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
package net.tridentsdk.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.tridentsdk.api.Material;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.nbt.NBTDecoder;
import net.tridentsdk.api.nbt.NBTException;

public class Slot implements Writable {

    private final int id;
    private final Material mat;

    private volatile short quantity;
    private volatile short damageValue;
    private volatile CompoundTag compoundTag;

    public Slot(ByteBuf buf) {
        this.id = (int) buf.readByte();
        this.mat = Material.fromString(String.valueOf(this.id));

        if (this.id == -1) {
            return;
        }

        this.quantity = (short) buf.readByte();
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
        this.id = is.getId();
        this.mat = is.getType();

        this.quantity = is.getQuantity();
        this.damageValue = is.getDamageValue();

        // TODO: build NBT data
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
}
