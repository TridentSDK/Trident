package net.tridentsdk.server.packets.login;

import io.netty.buffer.ByteBuf;

import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

public class PacketLoginInEncryptionResponse implements Packet {

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

        secretLength = buf.readShort();
        secret = new byte[secretLength];

        for(int i = 0; i <= secretLength; i++) {
            secret[i] = buf.readByte();
        }

        tokenLength = buf.readShort();
        token = new byte[tokenLength];

        for(int i = 0; i <= secretLength; i++) {
            token[i] = buf.readByte();
        }

        return this;
    }

    @Override
    public PacketType getType() {
        return PacketType.IN;
    }

    public short getSecretLength() {
        return secretLength;
    }

    public short getTokenLength() {
        return tokenLength;
    }

    public byte[] getSecret() {
        return secret;
    }

    public byte[] getToken() {
        return token;
    }

    @Override
    public void encode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketLoginInEncryptionResponse cannot be encoded!");
    }

    @Override
    public void handleOutbound(ClientConnection connection) {
        //
    }
}
