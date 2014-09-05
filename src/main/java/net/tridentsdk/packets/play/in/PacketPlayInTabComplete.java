package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInTabComplete extends InPacket {

    private String text;
    private boolean hasPosition;
    private Location lookedAtBlock;

    @Override
    public int getId() {
        return 0x14;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.text = Codec.readString(buf);
        this.hasPosition = buf.readBoolean();

        if(hasPosition) {
            long encoded = buf.readLong();
            double x = encoded << 38;
            double y = encoded << 26 >> 52;
            double z = encoded << 38 >> 38;

            this.lookedAtBlock = new Location(null, x, y, z);
        }

        return this;
    }

    public String getText() {
        return text;
    }

    public boolean isHasPosition() {
        return hasPosition;
    }

    public Location getLookedAtBlock() {
        return lookedAtBlock;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
