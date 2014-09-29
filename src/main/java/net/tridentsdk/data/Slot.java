/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.tridentsdk.api.Material;
import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.nbt.NBTDecoder;
import net.tridentsdk.api.nbt.NBTException;

/**
 * Represents one of the slots in an inventory
 *
 * @author The TridentSDK Team
 */
public class Slot implements Writable {
    private final int id;
    private final Material mat;

    private volatile short quantity;
    private volatile short damageValue;
    private volatile CompoundTag compoundTag;

    /**
     * Creates a new slot based on serialized data from a ByteBuf
     *
     * @param buf the buffer to deserialize information from
     */
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
