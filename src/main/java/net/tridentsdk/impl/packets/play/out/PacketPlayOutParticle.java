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
import net.tridentsdk.api.Location;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.OutPacket;

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
