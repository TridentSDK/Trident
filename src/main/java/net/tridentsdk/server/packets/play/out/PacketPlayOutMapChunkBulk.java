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
import net.tridentsdk.server.data.ChunkMetaBuilder;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutMapChunkBulk extends OutPacket {

    protected boolean lightSent;
    protected int columnCount;
    protected ChunkMetaBuilder meta;
    protected byte[] data;

    @Override
    public int getId() {
        return 0x26;
    }

    public boolean isLightSent() {
        return this.lightSent;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public ChunkMetaBuilder getMeta() {
        return this.meta;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeBoolean(this.lightSent);

        Codec.writeVarInt32(buf, this.columnCount);
        this.meta.write(buf);

        buf.writeBytes(this.data);
    }
}
