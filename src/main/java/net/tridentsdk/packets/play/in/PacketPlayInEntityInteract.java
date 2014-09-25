/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInEntityInteract extends InPacket {

    /**
     * Entity id of the target interacted
     */
    protected int target;
    /**
     * Type of interation, reference InteractType
     *
     * @see net.tridentsdk.packets.play.in.PacketPlayInEntityInteract.InteractType
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

        private int id;

        InteractType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static InteractType fromId(int id) {
            for(InteractType type : values()) {
                if(type.getId() == id)
                    return type;
            }

            return null;
        }
    }
}
