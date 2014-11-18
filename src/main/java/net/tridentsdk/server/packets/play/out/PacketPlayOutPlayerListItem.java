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
package net.tridentsdk.server.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

import java.util.UUID;

public class PacketPlayOutPlayerListItem extends OutPacket {

    protected int action;
    protected PlayerListDataBuilder[] playerListData;

    @Override
    public int getId() {
        return 0x37;
    }

    public int getAction() {
        return this.action;
    }

    public PlayerListDataBuilder[] getPlayerListData() {
        return this.playerListData;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.action);
        Codec.writeVarInt32(buf, this.playerListData.length);

        for (PlayerListDataBuilder data : this.playerListData) {
            data.write(buf);
        }
    }

    public class PlayerListDataBuilder {
        protected UUID id;
        protected Object[] values;

        public UUID getId() {
            return this.id;
        }

        public PlayerListDataBuilder setId(UUID id) {
            this.id = id;

            return this;
        }

        public Object[] getValues() {
            return this.values;
        }

        public PlayerListDataBuilder setValues(Object... values) {
            this.values = values;

            return this;
        }

        public void write(ByteBuf buf) {
            buf.writeLong(this.id.getMostSignificantBits());
            buf.writeLong(this.id.getLeastSignificantBits());

            // rip in organize
            for (Object o : this.values) {
                switch (o.getClass().getSimpleName()) {
                    case "String":
                        Codec.writeString(buf, (String) o);
                        break;

                    case "Integer":
                        Codec.writeVarInt32(buf, (Integer) o);
                        break;

                    case "Boolean":
                        buf.writeBoolean((Boolean) o);
                        break;

                    default:
                        // ignore bad developers
                        break;
                }
            }
        }
    }
}
