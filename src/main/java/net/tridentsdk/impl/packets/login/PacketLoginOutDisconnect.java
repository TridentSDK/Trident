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
package net.tridentsdk.impl.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.Packet;
import net.tridentsdk.impl.netty.packet.PacketType;

/**
 * Packet used to disconnect the client from the login stage for whatsoever reason
 *
 * @author The TridentSDK Team
 */
public class PacketLoginOutDisconnect implements Packet {
    /**
     * Disconnect message, represented JSON
     */
    protected String jsonMessage;

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
        Codec.writeString(buf, this.jsonMessage);
    }

    @Override
    public void handleReceived(ClientConnection connection) {
    }

    // Here too...
    public String getJsonMessage() {
        return this.jsonMessage;
    }

    public Packet setJsonMessage(String jsonMessage) {
        this.jsonMessage = jsonMessage;
        return this;
    }

    /**
     * {@inheritDoc} <p/> <p>Cannot be decoded</p>
     */
    @Override
    public Packet decode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketLoginOutDisconnect cannot be encoded!");
    }
}
