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

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class PacketPlayOutMapChunkBulk extends OutPacket {

    protected final Queue<PacketPlayOutChunkData> entries = new PriorityBlockingQueue<>(1024,
            new Comparator<PacketPlayOutChunkData>() {
                @Override
                public int compare(PacketPlayOutChunkData o1, PacketPlayOutChunkData o2) {
                    ChunkLocation c = o1.chunkLocation();
                    ChunkLocation c0 = o2.chunkLocation();

                    int cx = c.x();
                    int cz = c.z();

                    int c0x = c0.x();
                    int c0z = c0.z();

                    return (Math.abs(cx) + Math.abs(cz)) - (Math.abs(c0x) + Math.abs(c0z));
                }
            });
    protected boolean lightSent = true;

    @Override
    public int id() {
        return 0x26;
    }

    public void addEntry(PacketPlayOutChunkData entry) {
        entries.add(entry);
    }

    public boolean hasEntries() {
        return !entries.isEmpty();
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeBoolean(this.lightSent);

        Codec.writeVarInt32(buf, entries.size());

        for (PacketPlayOutChunkData packet : entries) {
            ChunkLocation location = packet.chunkLocation();

            buf.writeInt(location.x());
            buf.writeInt(location.z());
            buf.writeShort(packet.bitmask());
        }

        for (PacketPlayOutChunkData packet : entries) {
            buf.writeBytes(packet.data());
        }
    }
}
