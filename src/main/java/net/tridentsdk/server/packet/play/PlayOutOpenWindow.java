package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.server.inventory.TridentInventory;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.wstr;

/**
 * Opens an inventory window for the client.
 */
@Immutable
public class PlayOutOpenWindow extends PacketOut {
    /**
     * The inventory window to open
     */
    private final TridentInventory inventory;
    /**
     * The horse, if this inventory is a horse inventory
     */
    private final Entity entity;

    public PlayOutOpenWindow(TridentInventory inventory, Entity entity) {
        super(PlayOutOpenWindow.class);
        this.inventory = inventory;
        this.entity = entity;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.inventory.getId());
        wstr(buf, this.inventory.getType().toString());
        wstr(buf, this.inventory.getTitle().toString());
        buf.writeByte(this.inventory.getSize());

        if (this.inventory.getType() == InventoryType.HORSE) {
            buf.writeInt(this.entity.getId()); // y no varint, mojang
        }
    }
}
