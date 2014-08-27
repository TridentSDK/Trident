package net.tridentsdk.server.packets.login;

import io.netty.buffer.ByteBuf;

import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

/*
 * TODO: Figure out a safe-way to pass on player's name
 */
public class PacketLoginInStart implements Packet {

    private String name;

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        name = Codec.readString(buf);

        return this;
    }

    @Override
    public PacketType getType() {
        return PacketType.IN;
    }

    public String getName() {
        return name;
    }

    @Override
    public void encode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketLoginInStart cannot be encoded!");
    }

    @Override
    public void handleOutbound(ClientConnection connection) {
        // TODO: Respond with PacketLoginOutEncryptionRequest
    }
}
