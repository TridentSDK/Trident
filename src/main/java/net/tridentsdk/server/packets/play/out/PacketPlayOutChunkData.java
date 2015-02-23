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

package net.tridentsdk.server.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.world.ChunkLocation;

public class PacketPlayOutChunkData extends OutPacket {
    protected byte[] data;
    protected ChunkLocation chunkLocation;
    protected boolean continuous;
    protected short bitmask;

    // Do not read this
    private final Object barrier;

    public PacketPlayOutChunkData() {
        barrier = this;
    }

    public PacketPlayOutChunkData(byte[] data, ChunkLocation chunkLocation, boolean continuous, short bitmask) {
        this.data = data;
        this.chunkLocation = chunkLocation;
        this.continuous = continuous;
        this.bitmask = bitmask;

        barrier = this;
    }

    @Override
    public int id() {
        return 0x21;
    }

    public ChunkLocation chunkLocation() {
        return this.chunkLocation;
    }

    public boolean continuous() {
        return this.continuous;
    }

    public short bitmask() {
        return this.bitmask;
    }

    public byte[] data() {
        return this.data;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(this.chunkLocation.x());
        buf.writeInt(this.chunkLocation.z());

        buf.writeBoolean(this.continuous);
        buf.writeByte((int) this.bitmask);

        Codec.writeVarInt32(buf, this.data.length);
        buf.writeBytes(this.data);
    }
}
