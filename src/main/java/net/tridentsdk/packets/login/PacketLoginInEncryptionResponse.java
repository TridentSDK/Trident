/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;
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
