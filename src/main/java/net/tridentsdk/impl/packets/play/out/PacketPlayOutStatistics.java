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

public class PacketPlayOutStatistics extends OutPacket {

    protected StatisticEntry[] entries;

    @Override
    public int getId() {
        return 0x37;
    }

    public StatisticEntry[] getEntries() {
        return this.entries;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.entries.length);

        for (StatisticEntry entry : this.entries) {
            entry.write(buf);
        }
    }

    public class StatisticEntry {
        protected String string;
        protected int value;

        public String getString() {
            return this.string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public int getValue() {
            return this.value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void write(ByteBuf buf) {
            Codec.writeString(buf, this.string);
            Codec.writeVarInt32(buf, this.value);
        }
    }
}
