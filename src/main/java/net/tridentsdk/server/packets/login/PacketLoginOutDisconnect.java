package net.tridentsdk.server.packets.login;

import io.netty.buffer.ByteBuf;

import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

/*
 * TODO: Read up more on disconnect JSON message
 */
public class PacketLoginOutDisconnect implements Packet {

    private String jsonMessage;

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    @Override
    public void encode(ByteBuf buf) {
        // TODO (for now at-least)
    }

    public String getJsonMessage() {
        return jsonMessage;
    }

    public void setJsonMessage(String jsonMessage) {
        this.jsonMessage = jsonMessage;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        throw new UnsupportedOperationException("PacketLoginOutDisconnect cannot be encoded!");
    }

    @Override
    public void handleOutbound(ClientConnection connection) {
        throw new UnsupportedOperationException(
                "PacketLoginOutDisconnect is a client-bound packet therefor cannot be handled!");
    }
}
