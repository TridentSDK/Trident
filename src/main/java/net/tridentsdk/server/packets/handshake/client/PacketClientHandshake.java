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

package net.tridentsdk.server.packets.handshake.client;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;
import net.tridentsdk.server.netty.protocol.Protocol4;

/**
 * The login packet sent to connect the server to the client
 *
 * @author The TridentSDK Team
 */
public class PacketClientHandshake implements Packet {
    int    protocolVersion;
    String address;
    short  port;
    int    nextState;

    @Override
    public Packet decode(ByteBuf buf) {
        this.protocolVersion = Codec.readVarInt32(buf);
        this.address = Codec.readString(buf);
        this.port = buf.readShort();
        this.nextState = Codec.readVarInt32(buf);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cannot be encoded. Throws {@link UnsupportedOperationException}.</p>
     */
    @Override
    public ByteBuf encode() {
        throw new UnsupportedOperationException("PacketClientHandshake cannot be encoded!");
    }

    @Override
    public PacketType getType() {
        return Protocol4.Handshake.Client.HANDSHAKE;
    }
}
