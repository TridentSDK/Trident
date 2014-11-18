/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server.packets.handshake;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;
import net.tridentsdk.server.netty.protocol.Protocol;

/**
 * The handshake packet from the client defining information about said client
 *
 * @author The TridentSDK Team
 */
public class PacketHandshakeIn extends InPacket {
    /**
     * Protocol version the client is running on
     */
    protected int protocolVersion;

    /**
     * Beleived to stay as "localhost", more documentation required
     */
    protected String address;

    /**
     * Beleived to be always "25565", more documentation required
     */
    protected short port;

    /**
     * The next stage the client will be going into <p/> 1 for STATUS 2 for LOGIN
     */
    protected int nextState;

    @Override
    public Packet decode(ByteBuf buf) {
        this.protocolVersion = Codec.readVarInt32(buf);
        this.address = Codec.readString(buf);
        this.port = buf.readShort();
        this.nextState = Codec.readVarInt32(buf);
        return this;
    }

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    /**
     * {@inheritDoc} <p/> <p>Nothing is done here</p>
     */
    @Override
    public void handleReceived(ClientConnection connection) {
        switch (this.nextState) {
            case 1:
                connection.setStage(Protocol.ClientStage.STATUS);
                break;

            case 2:
                connection.setStage(Protocol.ClientStage.LOGIN);
                break;
        }
    }
}
