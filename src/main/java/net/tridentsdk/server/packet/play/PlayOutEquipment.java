package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.net.Slot;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.wvint;

/**
 * The packet is sent by the server to inform the client of
 * an entity's equipped armor and held item(s).
 */
@Immutable
public class PlayOutEquipment extends PacketOut {
    /**
     * The entity with the given equipment
     */
    private final TridentEntity entity;
    /**
     * The slot number
     *
     * - 0 = main
     * - 1 = off
     * - 2 = boots
     * ...
     * - 5 = helmet
     */
    private final int slot;
    /**
     * The item to place in that slot
     */
    private final Item item;

    public PlayOutEquipment(TridentEntity entity, int slot, Item item) {
        super(PlayOutEquipment.class);
        this.entity = entity;
        this.slot = slot;
        this.item = item;
    }

    @Override
    public void write(ByteBuf buf) {
        wvint(buf, this.entity.getId());
        wvint(buf, this.slot);
        Slot.newSlot(this.item).write(buf);
    }
}