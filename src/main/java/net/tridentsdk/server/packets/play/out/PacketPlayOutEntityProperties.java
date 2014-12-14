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
import net.tridentsdk.server.data.PropertyBuilder;
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
