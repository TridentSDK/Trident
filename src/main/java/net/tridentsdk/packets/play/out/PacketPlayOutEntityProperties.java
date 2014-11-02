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
import net.tridentsdk.data.PropertyBuilder;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutEntityProperties extends OutPacket {

    protected int entityId;
    protected PropertyBuilder[] properties = {};

    @Override
    public int getId() {
        return 0x20;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public PropertyBuilder[] getProperties() {
        return this.properties;
    }

    public void cleanup() {
        PropertyBuilder[] newProperties = {};

        for (PropertyBuilder value : this.properties) {
            if (value != null) {
                newProperties[newProperties.length] = value;
            }
        }

        this.properties = newProperties;
    }

    @Override
    public void encode(ByteBuf buf) {
        this.cleanup();

        Codec.writeVarInt32(buf, this.entityId);
        buf.writeInt(this.properties.length);

        for (PropertyBuilder property : this.properties) {
            property.write(buf);
        }
    }
}
