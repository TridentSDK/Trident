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
package net.tridentsdk.impl.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.encryption.RSA;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.InPacket;
import net.tridentsdk.impl.netty.packet.Packet;
import net.tridentsdk.impl.netty.packet.PacketType;

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
             * Note: A notchian Minecraft impl will have one KeyPair for all clients during the LOGIN stage,
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
