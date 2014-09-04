package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInChat extends InPacket {

    private String message;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.message = Codec.readString(buf);

        return this;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
    }
}
