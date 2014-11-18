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
import net.tridentsdk.api.world.ChunkLocation;
import net.tridentsdk.server.data.RecordBuilder;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutMultiBlockChange extends OutPacket {

    protected ChunkLocation chunkLocation;
    protected RecordBuilder[] records = {};

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
        RecordBuilder[] newRecords = {};

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
