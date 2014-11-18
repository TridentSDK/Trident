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
import net.tridentsdk.impl.netty.packet.OutPacket;

public class PacketPlayOutAttachEntity extends OutPacket {

    protected int entityId;
    protected int vehicleId;
    protected boolean leash;

    @Override
    public int getId() {
        return 0x0B;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getVehicleId() {
        return this.vehicleId;
    }

    public boolean isLeashed() {
        return this.leash;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(this.entityId); // Well, that's a first
        buf.writeInt(this.vehicleId); // AGAIN
        // rip in peace varints

        buf.writeBoolean(this.leash);
    }
}
