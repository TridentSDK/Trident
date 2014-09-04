package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * IM IN ME MUMS CAAR
 */
public class PacketPlayInSteerVehicle extends InPacket {

    private float sideways; // I don't even
    private float forward;  // mojang pls

    private short flags;

    @Override
    public int getId() {
        return 0x0C;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        // VROOM VROOM
        this.sideways = buf.readFloat();

        // UR 2 SLOW!?!?!?
        this.forward = buf.readFloat();

        // fkn CTF in COD
        flags = buf.readUnsignedByte();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Respond to the client accordingly
    }
}
