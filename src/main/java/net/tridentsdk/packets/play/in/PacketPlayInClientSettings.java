package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

import java.util.Locale;

public class PacketPlayInClientSettings extends InPacket {

    private Locale locale;
    private short viewDistance;
    private byte chatFlags;
    private boolean chatColors;

    @Override
    public int getId() {
        return 0x15;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.locale = Locale.forLanguageTag(Codec.readString(buf));
        this.viewDistance = buf.readByte();
        this.chatFlags = buf.readByte();
        this.chatColors = buf.readBoolean();

        buf.readUnsignedByte(); // -shrugs-

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
