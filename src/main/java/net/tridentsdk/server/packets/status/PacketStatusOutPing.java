package net.tridentsdk.server.packets.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

public class PacketStatusOutPing implements Packet {

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeLong(System.currentTimeMillis());
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketStatusOutResponse cannot be decoded!");
    }

    @Override
    public void handleOutbound(ClientConnection connection) {
        throw new UnsupportedOperationException("PacketStatusOutResponse is a client-bound packet therefor cannot be handled!");
    }
}
