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
import io.netty.buffer.Unpooled;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.world.NewChunkSection;
import net.tridentsdk.world.ChunkLocation;

public class PacketPlayOutChunkData extends OutPacket {

    protected NewChunkSection[] chunkSections;
    protected ChunkLocation chunkLocation;
    protected boolean continuous;
    protected int bitmask;

    public PacketPlayOutChunkData() {
    }

    public PacketPlayOutChunkData(NewChunkSection[] chunkSections, ChunkLocation chunkLocation, boolean continuous, int bitmask) {
        this.chunkSections = chunkSections;
        this.chunkLocation = chunkLocation;
        this.continuous = continuous;
        this.bitmask = bitmask;
    }

    @Override
    public int id() {
        return 0x20;
    }

    public ChunkLocation chunkLocation() {
        return this.chunkLocation;
    }

    public boolean continuous() {
        return this.continuous;
    }

    public int bitmask() {
        return this.bitmask;
    }

    @Override
    public void encode(ByteBuf output) {
        try {
            output.writeInt(chunkLocation.x());
            output.writeInt(chunkLocation.z());
            output.writeBoolean(continuous);
            Codec.writeVarInt32(output, bitmask);

            ByteBuf buf = Unpooled.buffer();
            for (NewChunkSection section : chunkSections) {
                if(section == null) {
                    continue;
                }

                section.writeBlocks(buf);
                section.writeBlockLight(buf);

                if(section.hasSkyLight()) {
                    section.writeSkyLight(buf);
                }
            }

            buf.readerIndex(0);
            Codec.writeVarInt32(output, buf.readableBytes() + (continuous ? 256 : 0));
            output.writeBytes(buf);
            buf.release();

            if(continuous) {
                // TODO Write biome data
                for (int i = 0; i < 256; i++) {
                    output.writeByte(1);
                }
            }

            Codec.writeVarInt32(output, 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
