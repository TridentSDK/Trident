/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.tridentsdk.api.Material;
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

    public int getId() {
        return this.id;
    }

    public Material getType() {
        return this.mat;
    }

    public short getQuantity() {
        return this.quantity;
    }

    public short getDamageValue() {
        return this.damageValue;
    }

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
