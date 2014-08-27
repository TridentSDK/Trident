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

package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Used to represent any erroneous inPackets received
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class UnknownPacket implements Packet {
    @Override
    public Packet decode(ByteBuf buf) {
        return this;
    }

    /**
     * {@inheritDoc} <p/> <p>Cannot be encoded. Throws UnsupportedOperationException</p>
     */
    @Override public void encode(ByteBuf buf) {
        throw new UnsupportedOperationException("Cannot serialize unknown packet");
    }

    @Override
    public int getId() {
        return -1;
    }

    /**
     * {@inheritDoc} <p/> <p>Returns {@code null}, since we don't know where the packet came from</p>
     */
    @Override public PacketType getType() {
        return null;
    }

    /**
     * {@inheritDoc} <p/> <p>Does not do anything</p>
     */
    @Override
    public void handleOutbound(ClientConnection connection) {
    }
}
