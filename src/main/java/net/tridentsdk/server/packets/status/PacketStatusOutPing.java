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
public class PacketStatusOutPing implements Packet {
    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeLong(System.currentTimeMillis());
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cannot be decoded</p>
     */
    @Override
    public Packet decode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketStatusOutResponse cannot be decoded!");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cannot be handled</p>
     */
    @Override
    public void handleOutbound(ClientConnection connection) {
        throw new UnsupportedOperationException(
                "PacketStatusOutResponse is a client-bound packet therefore cannot be handled!");
    }
}
