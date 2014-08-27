package net.tridentsdk.server.packets.login;

import io.netty.buffer.ByteBuf;

import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

public class PacketLoginOutSuccess implements Packet {

    @Override
    public int getId() {
        return 0x02;
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    @Override
    public void encode(ByteBuf buf) {
        // TODO: Encode packet
    }

    @Override
    public Packet decode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketLoginOutSuccess cannot be encoded!");
    }

    @Override
    public void handleOutbound(ClientConnection connection) {
        throw new UnsupportedOperationException(
                "PacketLoginOutSuccess is a client-bound packet therefor cannot be handled!");
    }
}
