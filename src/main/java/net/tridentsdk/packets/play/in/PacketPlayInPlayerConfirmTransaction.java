package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerConfirmTransaction extends InPacket {

    private short actionNumber;
    private boolean accepted;

    @Override
    public int getId() {
        return 0x0F;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        buf.readByte(); //ignore windowId, we'd have the window the player has open anyhow

        this.actionNumber = buf.readShort();
        this.accepted = buf.readBoolean();

        return this;
    }

    public short getActionNumber() {
        return actionNumber;
    }

    public boolean isAccepted() {
        return accepted;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
