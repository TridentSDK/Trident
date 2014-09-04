package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInEntityInteract extends InPacket {

    private int target;
    private int type; // TODO: Change to InteractType

    private Location location;

    @Override
    public int getId() {
        return 0x02;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.target = Codec.readVarInt32(buf);
        this.type = Codec.readVarInt32(buf);

        double x = buf.readFloat();
        double y = buf.readFloat();
        double z = buf.readFloat();

        this.location = new Location(null, x, y, z); // TODO: Get the clients world
        return this;
    }

    public int getTarget() {
        return target;
    }

    public int getInteractType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Respond to the client accordingly
    }
}
