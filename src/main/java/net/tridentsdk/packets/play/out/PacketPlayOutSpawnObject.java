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
package net.tridentsdk.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.EntityType;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutSpawnObject extends OutPacket {

    protected int entityId;
    protected EntityType type;
    protected Entity entity;
    // TODO: Object data

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public void encode(ByteBuf buf) {
        Location l = this.entity.getLocation();
        Vector v = this.entity.getVelocity();

        Codec.writeVarInt32(buf, this.entityId);
        buf.writeByte(this.type.ordinal()); // TODO: Get the correct id type

        buf.writeInt((int) l.getX() * 32);
        buf.writeInt((int) l.getY() * 32);
        buf.writeInt((int) l.getZ() * 32);

        buf.writeByte((int) (byte) l.getYaw());
        buf.writeByte((int) (byte) l.getPitch());
        buf.writeByte((int) (byte) l.getPitch()); // -shrugs-

        buf.writeShort((int) v.getX());
        buf.writeShort((int) v.getY());
        buf.writeShort((int) v.getZ());
    }
}
