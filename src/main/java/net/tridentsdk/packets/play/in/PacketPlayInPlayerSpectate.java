package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerSpectate extends InPacket {

    @Override
    public int getId() {
        return 0x18;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        // Ignore the UUID, we know the client will be spectating
        buf.readLong();
        buf.readLong();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
