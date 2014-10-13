/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.encryption.RSA;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

/**
 * @author The TridentSDK Team
 */
public class PacketLoginInStart extends InPacket {
    /**
     * Username of the client to be verified
     */
    protected String name;

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.name = Codec.readString(buf);

        return this;
    }

    @Override
    public PacketType getType() {
        return PacketType.IN;
    }

    /**
     * Gets the client name
     *
     * @return the client name
     */
    public String getName() {
        return this.name;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        LoginManager.getInstance().initLogin(connection.getAddress(), this.getName());
        PacketLoginOutEncryptionRequest p = new PacketLoginOutEncryptionRequest();

        // Generate the 4 byte token and update the packet
        connection.generateToken();
        p.set("verifyToken", connection.getVerificationToken());

        try {
            /* Generate the 1024-bit encryption key specified for the client, only used during the LOGIN stage.
             * Note: A notchian Minecraft server will have one KeyPair for all clients during the LOGIN stage,
             * this is flawed as it won't be hard to decrypt the secret which is used as the key for all encryption
             * after LOGIN therefore generating a keypair for each client is much more secure
             */
            KeyPair pair = RSA.generate(1024);

            // Update the packet with the new key
            p.set("publicKey", pair.getPublic().getEncoded());
            connection.setLoginKeyPair(pair);
        } catch (NoSuchAlgorithmException ignored) {
        }

        // Send the packet to the client
        connection.sendPacket(p);
    }
}
