package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerEnchant extends InPacket {

    private byte enchantment;

    @Override
    public int getId() {
        return 0x11;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        buf.readByte(); // ignore window id, we'd already know
        this.enchantment = buf.readByte();

        return this;
    }

    public byte getEnchantment() {
        return enchantment;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
