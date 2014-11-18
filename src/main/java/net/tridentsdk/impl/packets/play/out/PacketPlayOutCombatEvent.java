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

public class PacketPlayOutCombatEvent extends OutPacket {

    protected short event;
    protected int entityId;

    protected short duration;

    protected int playerId;
    protected String message;

    @Override
    public int getId() {
        return 0x42;
    }

    public short getEvent() {
        return this.event;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public short getDuration() {
        return this.duration;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, (int) this.event);

        switch (this.event) {
            case 1:
                Codec.writeVarInt32(buf, (int) this.duration);
                buf.writeInt(this.entityId);
                break;

            case 2:
                Codec.writeVarInt32(buf, this.playerId);
                buf.writeInt(this.entityId);

                Codec.writeString(buf, this.message);
                break;
        }
    }
}
