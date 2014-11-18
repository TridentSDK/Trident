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

public class PacketPlayOutUpdateScore extends OutPacket {

    protected String itemName;
    protected UpdateType type;
    protected String scoreName;
    protected int value;

    @Override
    public int getId() {
        return 0x3C;
    }

    public String getItemName() {
        return this.itemName;
    }

    public UpdateType getUpdateType() {
        return this.type;
    }

    public String getScoreName() {
        return this.scoreName;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.itemName);
        buf.writeByte((int) this.type.toByte());

        if (this.type.b == 1) {
            return;
        }

        Codec.writeString(buf, this.scoreName);
        Codec.writeVarInt32(buf, this.value);
    }

    public enum UpdateType {
        CREATE(0),
        UPDATE(0),
        REMOVE(1);

        protected final byte b;

        UpdateType(int i) {
            this.b = (byte) i;
        }

        public byte toByte() {
            return this.b;
        }
    }
}
