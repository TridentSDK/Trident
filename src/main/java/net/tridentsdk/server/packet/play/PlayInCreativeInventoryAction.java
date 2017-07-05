package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.server.inventory.TridentPlayerInventory;
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

        TridentPlayerInventory inventory = client.getPlayer().getInventory();
        if (slot == -1) {
            Item drop = item.toItem();
            // TODO drop it
            return;
        }

        if (item.getId() == -1) {
            inventory.remove(slot, Integer.MAX_VALUE);
        } else {
            inventory.add(slot, item.toItem(), item.getCount());
        }
    }
}