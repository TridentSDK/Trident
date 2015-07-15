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
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.util.Vector;

public class PacketPlayOutEntityCompleteMove extends OutPacket {
    protected int entityId;
    protected Vector difference;
    protected float yaw;
    protected float pitch;
    protected boolean onGround;

    @Override
    public int id() {
        return 0x17;
    }

    public int entityId() {
        return this.entityId;
    }

    public Vector difference() {
        return this.difference;
    }

    public float yaw() {
        return this.yaw;
    }

    public float pitch() {
        return this.pitch;
    }

    public boolean flags() {
        return this.onGround;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.entityId);

        buf.writeByte((int) this.difference.x() * 32);
        buf.writeByte((int) this.difference.y() * 32);
        buf.writeByte((int) this.difference.z() * 32);

        buf.writeByte((int) this.yaw);
        buf.writeByte((int) this.pitch);

        buf.writeBoolean(onGround);
    }
}
