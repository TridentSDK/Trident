/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server.packets.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

/**
 * Represents a ping packet sent in from the client
 *
 * @author The TridentSDK Team
 */
public class PacketStatusInPing extends InPacket {
    private long time;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.time = Codec.readVarInt64(buf);

        return this;
    }

    @Override
    public void handleRecieved(ClientConnection connection) {
        connection.sendPacket(new PacketStatusOutPing());
    }

    /**
     * TODO not an expert on this lol - AgentTroll
     */
    public long getTime() {
        return this.time;
    }

    @Override
    public PacketType getType() {
        return PacketType.IN;
    }
}
