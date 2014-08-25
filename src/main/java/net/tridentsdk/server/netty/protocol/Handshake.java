package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.server.packets.handshake.client.PacketClientHandshake;

class Handshake extends PacketManager {

    Handshake() {
        super();

        packets.put(0x00, PacketClientHandshake.class);
    }
}