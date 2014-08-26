package net.tridentsdk.server.packets.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

public class PacketStatusOutResponse implements Packet {

    private String jsonResponse;

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public void encode(ByteBuf buf) {
        // TODO (for now at-least)
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }


    @Override
    public void handleOutbound(ClientConnection connection) {
        throw new UnsupportedOperationException("PacketStatusOutResponse is a client-bound packet therefor cannot be handled!");
    }

    @Override
    public Packet decode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketStatusOutResponse is cannot be decoded!");
    }
}
