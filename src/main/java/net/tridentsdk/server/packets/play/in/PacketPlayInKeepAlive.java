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
package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.player.PlayerConnection;

/**
 * Sent by the packet to keep the connection alive
 */
public class PacketPlayInKeepAlive extends InPacket {

    /**
     * Id of the keep alive packet, sent by the server
     */
    protected int keepAliveId;

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.keepAliveId = Codec.readVarInt32(buf);

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        PlayerConnection pc = (PlayerConnection) connection;

        pc.setKeepAliveId(-1, 0L);
    }
}
