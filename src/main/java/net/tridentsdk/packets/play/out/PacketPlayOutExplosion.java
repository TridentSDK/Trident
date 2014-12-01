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
package net.tridentsdk.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.data.RecordBuilder;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutExplosion extends OutPacket {

    protected Location loc;
    protected int recordCount;
    protected RecordBuilder[] records;
    protected Vector velocity;

    @Override
    public int getId() {
        return 0x27;
    }

    public Location getLoc() {
        return this.loc;
    }

    public int getRecordCount() {
        return this.recordCount;
    }

    public RecordBuilder[] getRecords() {
        return this.records;
    }

    public Vector getVelocity() {
        return this.velocity;
    }

    public void cleanup() {
        RecordBuilder[] newRecords = {};

        for (RecordBuilder builder : this.records) {
            if (builder != null) {
                newRecords[newRecords.length] = builder;
            }
        }

        this.records = newRecords;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeFloat((float) this.loc.getX());
        buf.writeFloat((float) this.loc.getY());
        buf.writeFloat((float) this.loc.getZ());
        buf.writeFloat(0.0F); // unused by client

        buf.writeInt(this.recordCount);

        for (RecordBuilder builder : this.records) {
            builder.write(buf);
        }

        buf.writeFloat((float) this.velocity.getX());
        buf.writeFloat((float) this.velocity.getY());
        buf.writeFloat((float) this.velocity.getZ());
    }
}
