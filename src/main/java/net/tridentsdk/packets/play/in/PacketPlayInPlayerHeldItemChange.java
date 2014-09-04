package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerHeldItemChange extends InPacket {

    private short slot;

    @Override
    public int getId() {
        return 0x09;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.slot = buf.readShort();


        return this;
    }

    public short getSlot() {
        return slot;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
