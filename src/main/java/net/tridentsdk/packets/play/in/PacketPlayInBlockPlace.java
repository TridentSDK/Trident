package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInBlockPlace extends InPacket {

    private Location location;
    private byte direction; // wat
    private Vector cursorPosition;

    @Override
    public int getId() {
        return 0x08;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        long encodedLocation = buf.readLong();

        this.location = new Location(null, encodedLocation >> 38, encodedLocation << 26 >> 52,
                encodedLocation << 38 >> 38);
        this.direction = buf.readByte();

        // ignore held item
        for(int i = 0; i < buf.readableBytes() - 3; i++) {
            buf.readByte();
        }

        double x = buf.readByte();
        double y = buf.readByte();
        double z = buf.readByte();

        this.cursorPosition = new Vector(x, y, z);
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public byte getDirection() {
        return direction;
    }

    public Vector getCursorPosition() {
        return cursorPosition;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
