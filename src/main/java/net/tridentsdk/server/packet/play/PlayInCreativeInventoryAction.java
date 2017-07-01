package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.Slot;
import net.tridentsdk.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;

/**
 * Sent by the client whenever an inventory action occurs
 * in creative mode.
 */
@Immutable
public class PlayInCreativeInventoryAction extends PacketIn {
    public PlayInCreativeInventoryAction() {
        super(PlayInCreativeInventoryAction.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int slot = buf.readShort();
        Slot item = Slot.read(buf);

        System.out.println(item);
        if (item.getId() == -1) {
            // TODO drop item
        } else {
            client.getPlayer().getInventory().add(slot, item.toItem(), item.getCount());
        }
    }
}