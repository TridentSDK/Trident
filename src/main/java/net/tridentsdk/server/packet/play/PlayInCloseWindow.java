package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.inventory.TridentInventory;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;

@Immutable
public class PlayInCloseWindow extends PacketIn {
    public PlayInCloseWindow() {
        super(PlayInCloseWindow.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        short windowId = buf.readUnsignedByte();
        if (windowId == 0) {
            return; // Player inventory, no close needed
        }

        TridentInventory.close(windowId, client.getPlayer());
    }
}
