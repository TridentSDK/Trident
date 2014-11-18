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
import net.tridentsdk.Location;
import net.tridentsdk.util.Vector;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutParticle extends OutPacket {

    protected int particleId;
    protected boolean distance;
    protected Location loc;
    protected Vector offset; // d - (d * Random#nextGaussian())
    protected float particleData;
    protected int[] data;

    @Override
    public int getId() {
        return 0x2A;
    }

    public int getParticleId() {
        return this.particleId;
    }

    public boolean isDistance() {
        return this.distance;
    }

    public Location getLoc() {
        return this.loc;
    }

    public Vector getOffset() {
        return this.offset;
    }

    public float getParticleData() {
        return this.particleData;
    }

    public int[] getData() {
        return this.data;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(this.particleId);
        buf.writeBoolean(this.distance);

        buf.writeFloat((float) this.loc.getX());
        buf.writeFloat((float) this.loc.getY());
        buf.writeFloat((float) this.loc.getZ());

        buf.writeFloat((float) this.offset.getX());
        buf.writeFloat((float) this.offset.getY());
        buf.writeFloat((float) this.offset.getZ());

        buf.writeFloat(this.particleData);
        buf.writeInt(this.data.length);

        for (int i : this.data) {
            Codec.writeVarInt32(buf, i);
        }
    }
}
