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
import net.tridentsdk.server.data.RecordBuilder;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.world.ChunkLocation;

public class PacketPlayOutMultiBlockChange extends OutPacket {

    protected ChunkLocation chunkLocation;
    protected RecordBuilder[] records = { };

    @Override
    public int getId() {
        return 0x22;
    }

    public ChunkLocation getChunkLocation() {
        return this.chunkLocation;
    }

    public RecordBuilder[] getRecords() {
        return this.records;
    }

    public void cleanup() {
        RecordBuilder[] newRecords = { };

        for (RecordBuilder value : this.records) {
            if (value != null) {
                newRecords[newRecords.length] = value;
            }
        }

        this.records = newRecords;
    }

    @Override
    public void encode(ByteBuf buf) {
        this.cleanup();

        buf.writeInt(this.chunkLocation.getX());
        buf.writeInt(this.chunkLocation.getZ());

        Codec.writeVarInt32(buf, this.records.length);

        for (RecordBuilder record : this.records) {
            record.write(buf);
        }
    }
}
