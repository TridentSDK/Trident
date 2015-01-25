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

package net.tridentsdk.server.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketDirection;
import net.tridentsdk.util.TridentLogger;

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
    public int id() {
        return 0x00;
    }

    @Override
    public PacketDirection direction() {
        return PacketDirection.OUT;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.jsonMessage);
    }

    @Override
    public void handleReceived(ClientConnection connection) {
    }

    // Here too...
    public String jsonMessage() {
        return this.jsonMessage;
    }

    public Packet setJsonMessage(String jsonMessage) {
        this.jsonMessage = jsonMessage;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cannot be decoded</p>
     */
    @Override
    public Packet decode(ByteBuf buf) {
        TridentLogger.error(new UnsupportedOperationException("PacketLoginOutDisconnect cannot be encoded!"));
        return null;
    }
}
