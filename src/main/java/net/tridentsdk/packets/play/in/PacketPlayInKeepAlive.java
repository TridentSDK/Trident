package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInKeepAlive extends InPacket {

    private int keepAliveId;

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.keepAliveId = Codec.readVarInt32(buf);

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: State that keep alive has been done successfully
    }
}
