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
package net.tridentsdk.impl.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.InPacket;
import net.tridentsdk.impl.netty.packet.Packet;

public class PacketPlayInEntityInteract extends InPacket {

    /**
     * Entity id of the target interacted
     */
    protected int target;
    /**
     * Type of interation, reference InteractType
     *
     * @see net.tridentsdk.impl.packets.play.in.PacketPlayInEntityInteract.InteractType
     */
    protected InteractType type;

    /**
     * Location of the target, sent as 3 floats x, y, z
     */
    protected Location location;

    @Override
    public int getId() {
        return 0x02;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.target = Codec.readVarInt32(buf);
        this.type = InteractType.fromId(Codec.readVarInt32(buf));

        double x = (double) buf.readFloat();
        double y = (double) buf.readFloat();
        double z = (double) buf.readFloat();

        this.location = new Location(null, x, y, z); // TODO: Get the clients world
        return this;
    }

    public int getTarget() {
        return this.target;
    }

    public InteractType getInteractType() {
        return this.type;
    }

    public Location getLocation() {
        return this.location;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Respond to the client accordingly
    }

    public enum InteractType {
        INTERACT(0),
        ATTACK(1),
        INTERACT_AT(2);

        private final int id;

        InteractType(int id) {
            this.id = id;
        }

        public static InteractType fromId(int id) {
            for (InteractType type : InteractType.values()) {
                if (type.getId() == id)
                    return type;
            }

            return null;
        }

        public int getId() {
            return this.id;
        }
    }
}
