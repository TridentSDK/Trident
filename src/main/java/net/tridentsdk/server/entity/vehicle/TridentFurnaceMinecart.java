package net.tridentsdk.server.entity.vehicle;

import net.tridentsdk.Position;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.vehicle.FurnaceMinecart;
import net.tridentsdk.window.Window;
import net.tridentsdk.window.inventory.Item;

import java.util.UUID;

/**
 * Represents a minecart that holds a furnace
 *
 * @author The TridentSDK Team
 */
public class TridentFurnaceMinecart extends TridentMinecart implements FurnaceMinecart {
    public TridentFurnaceMinecart(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public int fuelTicks() {
        return 0;
    }

    @Override
    public void setFuelTicks(int ticks) {

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
        return EntityType.FURNANCE_MINECART;
    }
}
