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
package net.tridentsdk.server.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.netty.packet.PacketType;

/**
 * TODO not an expert on this - AgentTroll
 *
 * @author The TridentSDK Team
 */
public class PacketLoginOutSuccess extends OutPacket {
    /**
     * UUID of the client, represented as a String and contains dashes
     */
    protected String uuid;
    /**
     * Username of the client
     */
    protected String username;
    /**
     * Connection of the client, currently not used
     */
    protected ClientConnection connection;

    @Override
    public int getId() {
        return 0x02;
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    public ClientConnection getConnection() {
        return this.connection;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.uuid);
        Codec.writeString(buf, this.username);
    }
}
