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
import net.tridentsdk.server.data.RecordBuilder;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.util.Vector;

public class PacketPlayOutExplosion extends OutPacket {
    protected Position loc;
    protected int recordCount;
    protected RecordBuilder[] records;
    protected Vector velocity;

    @Override
    public int id() {
        return 0x27;
    }

    public Position location() {
        return this.loc;
    }

    public int recordCount() {
        return this.recordCount;
    }

    public RecordBuilder[] records() {
        return this.records;
    }

    public Vector velocity() {
        return this.velocity;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeFloat((float) this.loc.x());
        buf.writeFloat((float) this.loc.y());
        buf.writeFloat((float) this.loc.z());
        buf.writeFloat(0.0F); // unused by client

        buf.writeInt(this.recordCount);

        for (RecordBuilder builder : this.records) {
            builder.write(buf);
        }

        buf.writeFloat((float) this.velocity.x());
        buf.writeFloat((float) this.velocity.y());
        buf.writeFloat((float) this.velocity.z());
    }
}
