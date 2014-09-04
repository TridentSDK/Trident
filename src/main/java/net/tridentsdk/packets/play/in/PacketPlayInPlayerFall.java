package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerFall extends InPacket {

    private boolean onGround;

    @Override
    public int getId() {
        return 0x03;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.onGround = buf.readBoolean();

        return this;
    }

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Update the player's data
    }
}
