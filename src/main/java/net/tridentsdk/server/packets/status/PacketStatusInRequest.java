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
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

/**
 * TODO not an expert on this lol - AgentTroll
 *
 * @author The TridentSDK Team
 */
public class PacketStatusInRequest implements Packet {
    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        // No fields are in this packet, therefor no need for any decoding

        return this;
    }

    @Override
    public PacketType getType() {
        return PacketType.IN;
    }

    /**
     * {@inheritDoc} <p/> <p>Cannot be encoded</p>
     */
    @Override
    public void encode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketStatusInRequest cannot be encoded!");
    }

    @Override
    public void handleOutbound(ClientConnection connection) {
        // TODO Respond to the client accordingly
    }
}
