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
package net.tridentsdk.impl.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.OutPacket;
import net.tridentsdk.impl.netty.packet.PacketType;

/**
 * @author The TridentSDK Team
 */
public class PacketLoginOutEncryptionRequest extends OutPacket {
    /**
     * Length of the public key
     */
    protected short keyLength;
    /**
     * Length of the verification token
     */
    protected short tokenLength;

    /**
     * Public Key used during the LOGIN stage, reference PacketLoginInEncrytionResponse regarding encryption for the
     * LOGIN stage
     *
     * @see PacketLoginInEncryptionResponse
     */
    protected byte[] publicKey;
    /**
     * Verification token used throughout the LOGIN stage
     */
    protected byte[] verifyToken;

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
        this.keyLength = (short) this.publicKey.length;

        this.tokenLength = (short) 4;

        Codec.writeString(buf, "");

        Codec.writeVarInt32(buf, (int) this.keyLength);
        buf.writeBytes(this.publicKey);

        Codec.writeVarInt32(buf, (int) this.tokenLength);
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
