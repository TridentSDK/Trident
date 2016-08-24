/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.packet.handshake;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.rstr;
import static net.tridentsdk.server.net.NetData.rvint;

/**
 * Handshake packet. Sent for both ping and as a state
 * trigger before login.
 *
 * <p>Packet is serverbound only</p>
 */
@Immutable
public final class HandshakeIn extends PacketIn {
    /**
     * Constructor which sets up the packet header details.
     */
    public HandshakeIn() {
        super(HandshakeIn.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        // Schema:
        // VarInt:version, String:address, VarInt:port,
        // VarInt:nextState
        int version = rvint(buf);
        String address = rstr(buf);
        int port = buf.readUnsignedShort();
        int nextState = rvint(buf);

        client.setState(NetClient.NetState.values()[nextState]);
    }
}