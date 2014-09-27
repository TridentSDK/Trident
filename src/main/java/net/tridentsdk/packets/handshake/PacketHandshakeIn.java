/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.packets.handshake;

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
