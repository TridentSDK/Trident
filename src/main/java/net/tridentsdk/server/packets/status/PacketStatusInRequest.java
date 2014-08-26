package net.tridentsdk.server.packets.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

public class PacketStatusInRequest implements Packet {

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        // No fields are in this packet, therefor no need for any decoding

        return this;
    }

    @Override
    public PacketType getType() {
        return PacketType.IN;
    }

    @Override
    public void encode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketStatusInRequest cannot be encoded!");
    }

    @Override
    public void handleOutbound(ClientConnection connection) {
        // TODO Respond to the client accordingly
    }
}
