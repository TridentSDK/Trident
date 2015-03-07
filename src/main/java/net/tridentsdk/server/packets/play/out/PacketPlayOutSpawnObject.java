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
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.EntityType;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.util.Vector;

public class PacketPlayOutSpawnObject extends OutPacket {
    protected int entityId;
    protected EntityType type;
    protected Entity entity;

    @Override
    public int id() {
        return 0x00;
    }

    @Override
    public void encode(ByteBuf buf) {
        Position l = this.entity.position();
        Vector v = this.entity.velocity();

        Codec.writeVarInt32(buf, this.entityId);
        buf.writeByte(this.type.ordinal()); // TODO: Get the correct id type

        buf.writeInt((int) l.x() * 32);
        buf.writeInt((int) l.y() * 32);
        buf.writeInt((int) l.z() * 32);

        buf.writeByte((int) (byte) l.yaw());
        buf.writeByte((int) (byte) l.pitch());
        buf.writeByte((int) (byte) l.pitch()); // -shrugs-

        buf.writeShort((int) v.x());
        buf.writeShort((int) v.y());
        buf.writeShort((int) v.z());
    }
}
