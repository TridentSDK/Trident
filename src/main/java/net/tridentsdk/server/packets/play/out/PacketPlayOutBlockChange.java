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
import net.tridentsdk.server.data.Position;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutBlockChange extends OutPacket {
    protected Coordinates location;
    protected int blockId;

    @Override
    public int getId() {
        return 0x23;
    }

    public Coordinates getLocation() {
        return this.location;
    }

    public int getBlockId() {
        return this.blockId;
    }

    @Override
    public void encode(ByteBuf buf) {
        new Position(this.location).write(buf);

        Codec.writeVarInt32(buf, this.blockId);
    }
}
