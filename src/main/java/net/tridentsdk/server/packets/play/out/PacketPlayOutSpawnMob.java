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
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.util.Vector;

public class PacketPlayOutSpawnMob extends OutPacket {
    protected int entityId;
    protected Entity entity;
    protected ProtocolMetadata metadata;

    @Override
    public int id() {
        return 0x03;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public void encode(ByteBuf buf) {
        Position loc = this.entity.position();
        Vector velocity = this.entity.velocity();

        Codec.writeVarInt32(buf, this.entityId);
        buf.writeByte((int) entity.type().asByte()); // TODO: use the real type id

        buf.writeInt((int) loc.x() * 32);
        buf.writeInt((int) loc.y() * 32);
        buf.writeInt((int) loc.z() * 32);

        buf.writeByte((int) (byte) loc.yaw());
        buf.writeByte((int) (byte) loc.pitch());
        buf.writeByte((int) (byte) loc.pitch()); // -shrugs-

        buf.writeShort((int) velocity.x());
        buf.writeShort((int) velocity.y());
        buf.writeShort((int) velocity.z());

        metadata.write(buf);
    }
}
