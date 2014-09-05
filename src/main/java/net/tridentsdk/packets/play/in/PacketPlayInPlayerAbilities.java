package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerAbilities extends InPacket {

    private byte flags;

    private float flyingSpeed;
    private float walkingSpeed;

    @Override
    public int getId() {
        return 0x13;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.flags = buf.readByte();

        this.flyingSpeed = buf.readFloat();
        this.walkingSpeed = buf.readFloat();

        return this;
    }

    public byte getFlags() {
        return flags;
    }

    public float getFlyingSpeed() {
        return flyingSpeed;
    }

    public float getWalkingSpeed() {
        return walkingSpeed;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
