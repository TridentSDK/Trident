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
package net.tridentsdk.server.packets.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.Defaults;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

/**
 * Packet sent by the client to request PacketStatusOutResponse
 *
 * @author The TridentSDK Team
 * @see net.tridentsdk.server.packets.status.PacketStatusOutResponse
 */
public class PacketStatusInRequest extends InPacket {
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

    @Override
    public void handleReceived(ClientConnection connection) {
        PacketStatusOutResponse packet = new PacketStatusOutResponse();
        PacketStatusOutResponse.Response response = packet.getResponse();

        // TODO: Make sure this is thread-safe
        // Set MOTD and max players based on the config TODO events
        response.description.text = TridentServer.getInstance().getConfig()
                .getString("motd", Defaults.MOTD);
        response.players.max = TridentServer.getInstance().getConfig()
                .getInt("max-players", Defaults.MAX_PLAYERS);

        packet.response = response;

        connection.sendPacket(packet);
    }
}
