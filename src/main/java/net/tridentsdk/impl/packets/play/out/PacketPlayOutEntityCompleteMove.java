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
package net.tridentsdk.impl.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.OutPacket;

public class PacketPlayOutEntityCompleteMove extends OutPacket {

    protected int entityId;
    protected Vector difference;
    protected float yaw;
    protected float pitch;
    protected byte flags;

    @Override
    public int getId() {
        return 0x08;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Vector getDifference() {
        return this.difference;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public byte getFlags() {
        return this.flags;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.entityId);

        buf.writeByte((int) this.difference.getX() * 32);
        buf.writeByte((int) this.difference.getY() * 32);
        buf.writeByte((int) this.difference.getZ() * 32);

        buf.writeByte((int) this.yaw);
        buf.writeByte((int) this.pitch);

        buf.writeByte((int) this.flags);
    }
}
