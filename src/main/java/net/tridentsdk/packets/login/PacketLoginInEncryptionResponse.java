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

package net.tridentsdk.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.*;
import net.tridentsdk.server.netty.protocol.Protocol;

public class PacketLoginInEncryptionResponse extends InPacket {
    private short secretLength;
    private short tokenLength;

    private byte[] secret;
    private byte[] token;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        // TODO: Figure a better workaround

        this.secretLength = buf.readShort();
        this.secret = new byte[(int) this.secretLength];

        for (int i = 0; i <= (int) this.secretLength; i++) {
            this.secret[i] = buf.readByte();
        }

        this.tokenLength = buf.readShort();
        this.token = new byte[(int) this.tokenLength];

        for (int i = 0; i <= (int) this.secretLength; i++) {
            this.token[i] = buf.readByte();
        }

        return this;
    }

    @Override
    public PacketType getType() {
        return PacketType.IN;
    }

    /**
     * Gets the length of the secret
     *
     * @return the secret length
     */
    public short getSecretLength() {
        return this.secretLength;
    }

    /**
     * Gets the length of the client token
     *
     * @return the token client length
     */
    public short getTokenLength() {
        return this.tokenLength;
    }

    /**
     * Gets the actual secret
     *
     * @return the secret
     */
    public byte[] getSecret() {
        return this.secret;
    }

    /**
     * Gets the client token
     *
     * @return the client token
     */
    public byte[] getToken() {
        return this.token;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        String name = LoginManager.getInstance().getName(connection.getAddress());
        PacketLoginOutSuccess packet = new PacketLoginOutSuccess();
        packet.setName(name);

        connection.sendPacket(packet);
        connection.setStage(Protocol.ClientStage.PLAY);
        LoginManager.getInstance().finish(connection.getAddress());
    }
}
