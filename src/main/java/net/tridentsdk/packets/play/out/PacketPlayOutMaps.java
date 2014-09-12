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

package net.tridentsdk.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutMaps extends OutPacket {

    private int itemDamage;
    private int scale;
    private int length;
    private byte[] icons; // array must be 3 * larger than length
    private byte columns;
    private byte rows;
    private byte x;
    private byte y;
    private int columnLength;
    private byte[] data;

    @Override
    public int getId() {
        return 0x34;
    }

    public int getItemDamage() {
        return this.itemDamage;
    }

    public int getScale() {
        return this.scale;
    }

    public int getLength() {
        return this.length;
    }

    public byte[] getIcons() {
        return this.icons;
    }

    public byte getColumns() {
        return this.columns;
    }

    public byte getRows() {
        return this.rows;
    }

    public byte getX() {
        return this.x;
    }

    public byte getY() {
        return this.y;
    }

    public int getColumnLength() {
        return this.columnLength;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.itemDamage);
        buf.writeByte(this.scale);
        Codec.writeVarInt32(buf, this.length);

        buf.writeBytes(this.icons);
        buf.writeByte((int) this.columns);

        if ((int) this.columns <= 0) {
            return;
        }

        buf.writeByte((int) this.rows);
        buf.writeByte((int) this.x);
        buf.writeByte((int) this.y);

        Codec.writeVarInt32(buf, this.columnLength);
        buf.writeBytes(this.data); // here I'm not sure if I'm doing it right
    }
}
