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

import java.util.LinkedList;
import java.util.Queue;

public class PacketPlayOutMapChunkBulk extends OutPacket {
    protected final Queue<PacketPlayOutChunkData> entries = new LinkedList<>();
    protected boolean lightSent = true;
    private int size;

    @Override
    public int id() {
        return 0x26;
    }

    public void addEntry(PacketPlayOutChunkData entry) {
        entries.offer(entry);
        size += 10 + entry.data.length;
    }

    public int size() {
        return size;
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
