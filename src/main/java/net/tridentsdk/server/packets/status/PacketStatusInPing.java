package net.tridentsdk.server.packets.status;

import io.netty.buffer.ByteBuf;

import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketStatusInPing implements Packet {

    private long time;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        time = Codec.readVarInt64(buf);

        return this;
    }

    @Override
    public void handleOutbound(ClientConnection connection) {
        connection.sendPacket(new PacketStatusOutPing().decode(connection.getChannel().alloc().buffer()));
    }

    public long getTime() {
        return time;
    }

    @Override
    public void encode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketStatusInPing cannot be encoded!");
    }
}
