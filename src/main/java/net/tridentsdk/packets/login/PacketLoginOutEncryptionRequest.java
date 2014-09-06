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
import net.tridentsdk.server.encryption.RSA;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.netty.packet.PacketType;

public class PacketLoginOutEncryptionRequest extends OutPacket {
    private short keyLength;
    private short tokenLength;

    private byte[] publicKey;
    private byte[] verifyToken;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    @Override
    public void encode(ByteBuf buf) {
        try {
            this.publicKey = RSA.generate(1024).getPublic().getEncoded();
            this.keyLength = (short) this.publicKey.length;

            this.verifyToken = new byte[0];
            this.tokenLength = (short) 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Codec.writeString(buf, "");

        buf.writeShort((int) this.keyLength);
        buf.writeBytes(this.publicKey);

        buf.writeShort((int) this.tokenLength);
        buf.writeBytes(this.verifyToken);
    }

    /**
     * Gets the length of the encryption key
     *
     * @return the encrypted key length
     */
    public short getKeyLength() {
        return this.keyLength;
    }

    /**
     * Gets the length of the client token
     *
     * @return the client token length
     */
    public short getTokenLength() {
        return this.tokenLength;
    }

    /**
     * The public encryption key
     *
     * @return the encryption key
     */
    public byte[] getPublicKey() {
        return this.publicKey;
    }

    /**
     * The verification token
     *
     * @return the verification token
     */
    public byte[] getVerifyToken() {
        return this.verifyToken;
    }
}
