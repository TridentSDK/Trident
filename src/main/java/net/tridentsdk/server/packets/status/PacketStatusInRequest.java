/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.packets.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.Defaults;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

/**
 * Packet sent by the client to request PacketStatusOutResponse
 *
 * @author The TridentSDK Team
 * @see PacketStatusOutResponse
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
