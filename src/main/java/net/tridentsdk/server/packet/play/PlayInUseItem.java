package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.event.player.PlayerInteractEvent;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.inventory.PlayerInventory;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.rvint;

@Immutable
public final class PlayInUseItem extends PacketIn {
    public PlayInUseItem() {
        super(PlayInUseItem.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        TridentPlayer player = client.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Item cur = rvint(buf) == 0 ? inventory.getHeldItem() : inventory.getOffHeldItem();
        TridentServer.getInstance().getEventController().dispatch(new PlayerInteractEvent(player), e -> {
            if (!e.isCancelled()) {
                // TODO actions
            }
        });
    }
}