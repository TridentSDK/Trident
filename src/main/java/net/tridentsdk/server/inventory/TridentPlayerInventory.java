package net.tridentsdk.server.inventory;

import lombok.Getter;
import lombok.Setter;
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.inventory.PlayerInventory;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Implements an inventory that is held by the player
 */
@ThreadSafe
public class TridentPlayerInventory extends TridentInventory implements PlayerInventory {
    /**
     * The currently selected hotbar slot
     */
    @Getter
    @Setter
    private volatile int selectedSlot;
    /**
     * The player receiving the inventory
     */
    private final NetClient client;

    public TridentPlayerInventory(NetClient client) {
        super(InventoryType.PLAYER, 46);
        this.client = client;
    }

    @Nonnull
    @Override
    public Item getHeldItem() {
        return this.get(36 + this.selectedSlot);
    }

    @Nonnull
    @Override
    public Item getOffHeldItem() {
        return this.get(45);
    }

    @Override
    protected void sendViewers(PacketOut packetOut) {
        this.client.sendPacket(packetOut);
    }
}