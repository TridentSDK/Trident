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
package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Packet is sent when player steers his vehicle
 */
public class PacketPlayInSteerVehicle extends InPacket {
    /**
     * Positive to the left of the player
     */
    protected float sideways;
    /**
     * Positive forward
     */
    protected float forward;

    /**
     * 0x1 Jump, 0x2 Unmount
     */
    protected short flags;

    @Override
    public int getId() {
        return 0x0C;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.sideways = buf.readFloat();
        this.forward = buf.readFloat();
        this.flags = buf.readUnsignedByte();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Respond to the client accordingly
    }
}
