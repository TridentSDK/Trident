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
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

import java.util.UUID;

/**
 * Packet sent when player is spectating, need to research more about this packet
 */
public class PacketPlayInPlayerSpectate extends InPacket {

    /**
     * Target player, this might imply that the player is able to set other onlinePlayers to spectate mode? If so, we'll need
     * to fix that
     */
    protected UUID uuid;

    @Override
    public int id() {
        return 0x18;
    }

    public UUID uniqueId() {
        return this.uuid;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.uuid = new UUID(buf.readLong(), buf.readLong());

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
