package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Confirms a teleport response from the client
 */
public class PacketPlayInTeleportConfirm extends InPacket {
    /**
     * The teleport id sent by the client
     */
    protected int teleportId;

    @Override
    public Packet decode(ByteBuf buf) {
        teleportId = Codec.readVarInt32(buf);
        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
    }

    @Override
    public int id() {
        return 0x00;
    }
}
