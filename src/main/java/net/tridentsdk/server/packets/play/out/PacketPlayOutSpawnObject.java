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
import net.tridentsdk.base.Position;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

import java.util.UUID;

public class PacketPlayOutSpawnObject extends OutPacket {
    protected int entityId;
    protected Entity entity;

    @Override
    public int id() {
        return 0x00;
    }

    @Override
    public void encode(ByteBuf buf) {
        Position l = this.entity.position();

        Codec.writeVarInt32(buf, this.entityId);

        UUID id = UUID.randomUUID(); // TODO What????

        buf.writeLong(id.getMostSignificantBits());
        buf.writeLong(id.getLeastSignificantBits());

        buf.writeByte(entity.type().asByte());

        buf.writeDouble((int) (l.x() * 32));
        buf.writeDouble((int) (l.y() * 32));
        buf.writeDouble((int) (l.z() * 32));

        buf.writeByte((int) (byte) l.pitch());
        buf.writeByte((int) (byte) l.yaw());

        // TODO object data
        buf.writeInt(0);

        buf.writeShort((int) (entity.velocity().x() * 8000));
        buf.writeShort((int) (entity.velocity().y() * 8000));
        buf.writeShort((int) (entity.velocity().z() * 8000));
    }
}
