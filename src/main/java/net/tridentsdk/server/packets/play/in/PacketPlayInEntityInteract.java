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

package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.Position;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.Nullable;

public class PacketPlayInEntityInteract extends InPacket {
    /**
     * Entity id of the target interacted
     */
    protected int target;
    /**
     * Type of interation, reference InteractType
     *
     * @see InteractType
     */
    protected InteractType type;

    /**
     * Location of the target, sent as 3 floats x, y, z
     */
    @Nullable
    protected Position location;

    @Override
    public int id() {
        return 0x02;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.target = Codec.readVarInt32(buf);
        this.type = InteractType.fromId(Codec.readVarInt32(buf));

        if (type == InteractType.INTERACT_AT) {
            double x = (double) buf.readFloat();
            double y = (double) buf.readFloat();
            double z = (double) buf.readFloat();
            this.location = Position.create(null, x, y, z);
        }

        return this;
    }

    public int target() {
        return this.target;
    }

    public InteractType interactType() {
        return this.type;
    }

    public Position location() {
        return this.location;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).player();

        if (location != null) {
            location.setWorld(player.world());
        }

        // TODO: call event and process interact
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
