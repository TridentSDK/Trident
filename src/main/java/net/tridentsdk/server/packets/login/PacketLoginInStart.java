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
