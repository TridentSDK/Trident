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

package net.tridentsdk.server.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

/*
 * TODO: Read up more on disconnect JSON message
 */

/**
 * TODO not an expert on this - AgentTroll
 *
 * @author The TridentSDK Team
 */
public class PacketLoginOutDisconnect implements Packet {
    private String jsonMessage;

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    @Override
    public void encode(ByteBuf buf) {
        // TODO (for now at-least)
    }

    // Here too...
    public String getJsonMessage() {
        return this.jsonMessage;
    }

    public void setJsonMessage(String jsonMessage) {
        this.jsonMessage = jsonMessage;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>Cannot be decoded</p>
     */
    @Override
    public Packet decode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketLoginOutDisconnect cannot be encoded!");
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>Cannot be handled</p>
     */
    @Override
    public void handleOutbound(ClientConnection connection) {
        throw new UnsupportedOperationException(
                "PacketLoginOutDisconnect is a client-bound packet therefor cannot be handled!");
    }
}
