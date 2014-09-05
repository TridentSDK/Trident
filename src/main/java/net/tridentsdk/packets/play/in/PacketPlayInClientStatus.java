package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInClientStatus extends InPacket {

    private short actionId;

    @Override
    public int getId() {
        return 0x15;
    }

    public short getActionId() {
        return actionId;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.actionId = buf.readUnsignedByte();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
