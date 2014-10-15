/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        RecordBuilder[] newRecords = { };

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
