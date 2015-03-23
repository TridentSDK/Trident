package net.tridentsdk.server.entity.vehicle;

import net.tridentsdk.Position;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.vehicle.HopperMinecart;
import net.tridentsdk.window.Window;
import net.tridentsdk.window.inventory.Item;

import java.util.UUID;

/**
 * Represents a minecart that holds a Hopper block
 *
 * @author The TridentSDK Team
 */
public class TridentHopperMinecart extends TridentMinecart implements HopperMinecart {
    public TridentHopperMinecart(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public int transferCooldown() {
        return 0;
    }

    @Override
    public void setTransferCooldown(int cooldown) {

    }

    @Override
    public Window inventory() {
        return null;
    }

    @Override
    public Item heldItem() {
        return null;
    }

    @Override
    public void setHeldItem(Item item) {

    }

    @Override
    public EntityType type() {
        return EntityType.HOPPER_MINECART;
    }
}
