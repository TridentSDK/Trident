package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerLook extends InPacket {

    private float newYaw;
    private float newPitch;

    private boolean onGround;

    @Override
    public int getId() {
        return 0x05;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.newYaw = buf.readFloat();
        this.newPitch = buf.readFloat();
        this.onGround = buf.readBoolean();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Update values
    }
}
