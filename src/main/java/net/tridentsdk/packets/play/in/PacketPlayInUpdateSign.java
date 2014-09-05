package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInUpdateSign extends InPacket {

    private Location signLocation;
    private String[] jsonContents = new String[4];

    @Override
    public int getId() {
        return 0x12;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        long encoded = buf.readLong();
        double x = encoded >> 38;
        double y = encoded << 26 >> 52;
        double z = encoded << 38 >> 38;

        this.signLocation = new Location(null, x, y, z);

        for(int i = 0; i <= 4; i++) {
            jsonContents[i] = Codec.readString(buf);
        }
        return this;
    }

    public Location getSignLocation() {
        return signLocation;
    }

    public String[] getJsonContents() {
        return jsonContents;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly (reminder: update world)
    }
}
