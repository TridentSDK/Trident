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
package net.tridentsdk.server.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.netty.packet.PacketType;

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
     * @see net.tridentsdk.server.packets.login.PacketLoginInEncryptionResponse
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
