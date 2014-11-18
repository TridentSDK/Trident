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
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.OutPacket;

public class PacketPlayOutEntityRelativeMove extends OutPacket {

    protected int entityId;
    protected Vector difference;
    protected boolean onGround;

    @Override
    public int getId() {
        return 0x15;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Vector getDifference() {
        return this.difference;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.entityId);

        buf.writeInt((int) this.difference.getX() * 32);
        buf.writeInt((int) this.difference.getY() * 32);
        buf.writeInt((int) this.difference.getZ() * 32);

        buf.writeBoolean(this.onGround);
    }
}
