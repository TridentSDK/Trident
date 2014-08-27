package net.tridentsdk.server.packets.login;

import io.netty.buffer.ByteBuf;

import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

public class PacketLoginOutEncryptionRequest implements Packet {

    private short keyLength;
    private short tokenLength;

    private byte[] publicKey;
    private byte[] verifyToken;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketLoginOutEncryptionRequest cannot be decoded!");
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    @Override
    public void encode(ByteBuf buf) {
        // TODO (for-now at least)
    }

    public short getKeyLength() {
        return keyLength;
    }

    public short getTokenLength() {
        return tokenLength;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

    @Override
    public void handleOutbound(ClientConnection connection) {
        throw new UnsupportedOperationException(
                "PacketStatusOutResponse is a client-bound packet therefor cannot be handled!");
    }
}
