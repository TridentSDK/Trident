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
