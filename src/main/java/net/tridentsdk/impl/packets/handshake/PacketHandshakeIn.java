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
package net.tridentsdk.impl.packets.handshake;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.InPacket;
import net.tridentsdk.impl.netty.packet.Packet;
import net.tridentsdk.impl.netty.packet.PacketType;
import net.tridentsdk.impl.netty.protocol.Protocol;

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
