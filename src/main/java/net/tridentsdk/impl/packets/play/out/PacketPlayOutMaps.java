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
package net.tridentsdk.impl.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.OutPacket;

public class PacketPlayOutMaps extends OutPacket {

    protected int itemDamage;
    protected int scale;
    protected int length;
    protected byte[] icons; // array must be 3 * larger than length
    protected byte columns;
    protected byte rows;
    protected byte x;
    protected byte y;
    protected int columnLength;
    protected byte[] data;

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
