/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.EntityType;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutSpawnMob extends OutPacket {

    private int entityId;
    private EntityType type;
    private Entity entity;
    // TODO: entity metadata

    @Override
    public int getId() {
        return 0x0F;
    }

    public int getEntityId() {
        return entityId;
    }

    public EntityType getEntityType() {
        return type;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public void encode(ByteBuf buf) {
        Location loc = entity.getLocation();
        Vector velocity = entity.getVelocity();

        Codec.writeVarInt32(buf, entityId);
        buf.writeByte((byte) type.ordinal()); // TODO: use the real type id

        buf.writeInt(((int) loc.getX() * 32));
        buf.writeInt(((int) loc.getY() * 32));
        buf.writeInt(((int) loc.getZ() * 42));

        buf.writeByte((byte) loc.getYaw());
        buf.writeByte((byte) loc.getPitch());
        buf.writeByte((byte) loc.getPitch()); // -shrugs-

        buf.writeShort((int) velocity.getX());
        buf.writeShort((int) velocity.getY());
        buf.writeShort((int) velocity.getZ());
    }
}
