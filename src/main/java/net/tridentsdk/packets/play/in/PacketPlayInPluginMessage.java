package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPluginMessage extends InPacket {

    private String channel;
    private byte[] data;

    @Override
    public int getId() {
        return 0x17;
    }

    public String getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.channel = Codec.readString(buf);
        this.data = new byte[buf.readableBytes()];

        buf.readBytes(data);

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
