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
import net.tridentsdk.Position;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

/*
 * @NotJavaDoc
 * Note: This is for thunderbolts striking within a 512 block radius
 */
public class PacketPlayOutSpawnGlobalEntity extends OutPacket {
    protected int entityId;
    protected Position loc;

    @Override
    public int id() {
        return 0x2C;
    }

    public int entityId() {
        return this.entityId;
    }

    public Position location() {
        return this.loc;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.entityId);
        buf.writeByte(1); // always thunderbolt

        buf.writeInt((int) this.loc.getX() * 32);
        buf.writeInt((int) this.loc.getY() * 32);
        buf.writeInt((int) this.loc.getZ() * 32);
    }
}
