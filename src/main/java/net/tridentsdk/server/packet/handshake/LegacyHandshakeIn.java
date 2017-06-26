package net.tridentsdk.server.packet.handshake;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;

/**
 * Legacy handshake packet that is usually sent when the
 * client's regular ping fails.
 */
@Immutable
public class LegacyHandshakeIn extends PacketIn {
    public LegacyHandshakeIn() {
        super(LegacyHandshakeIn.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        if (buf.readUnsignedByte() != 1) {
            throw new RuntimeException("Legacy handshake has the wrong schema");
        }
    }
}
