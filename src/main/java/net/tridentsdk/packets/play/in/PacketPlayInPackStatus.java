package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPackStatus extends InPacket {

    private String hash;
    private int result;

    @Override
    public int getId() {
        return 0x19;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.hash = Codec.readString(buf);
        this.result = Codec.readVarInt32(buf);

        return this;
    }

    public String getHash() {
        return hash;
    }

    public int getResult() {
        return result;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
