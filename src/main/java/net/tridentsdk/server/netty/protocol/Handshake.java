package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.server.packets.handshake.client.PacketClientHandshake;

class Handshake implements ProtocolType {
    private Out out;

    public class Out extends PacketManager {
        protected Out() {
            packets.put(0x00, PacketClientHandshake.class);
        }
    }

    public PacketManager getOut() {
        return out;
    }

    @Override
    public PacketManager getIn() {
        return null;
    }
}