package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerCloseWindow extends InPacket {

    private int id;

    @Override
    public int getId() {
        return 0x0D;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.id = buf.readByte();

        return this;
    }

    public int getWindowId() {
        return id;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
