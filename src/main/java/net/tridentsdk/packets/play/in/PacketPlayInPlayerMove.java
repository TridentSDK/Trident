package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerMove extends InPacket {

    protected Location location;
    protected boolean  onGround;

    @Override
    public int getId() {
        return 0x04;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        this.location = new Location(null, x, y, z); // TODO: Get the player's world

        this.onGround = buf.readBoolean();

        return this;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
