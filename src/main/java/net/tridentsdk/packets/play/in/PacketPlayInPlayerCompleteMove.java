package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerCompleteMove extends PacketPlayInPlayerMove {

    private float newYaw;
    private float newPitch;

    @Override
    public int getId() {
        return 0x06;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        super.location = new Location(null, x, y, z); // TODO: Get the player's world

        this.newYaw = buf.readFloat();
        this.newPitch = buf.readFloat();

        super.onGround = buf.readBoolean();
        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
