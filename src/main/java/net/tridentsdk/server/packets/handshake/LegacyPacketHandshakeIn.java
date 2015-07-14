package net.tridentsdk.server.packets.handshake;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class LegacyPacketHandshakeIn extends InPacket {

    @Override
    public Packet decode(ByteBuf buf) {
        buf.readByte(); // always 0x01
        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
    }

    @Override
    public int id() {
        return 0xFE;
    }
}
