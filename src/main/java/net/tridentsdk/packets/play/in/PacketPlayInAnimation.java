package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * What the fuck is this?
 */
public class PacketPlayInAnimation extends InPacket {

    @Override
    public int getId() {
        return 0x0A;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly?
    }
}
