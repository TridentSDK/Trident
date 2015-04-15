package net.tridentsdk.server.entity.vehicle;

import net.tridentsdk.Position;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.vehicle.TNTMinecart;

import java.util.UUID;

/**
 * Represents a minecart that holds a TNT block
 *
 * @author The TridentSDK Team
 */
public class TridentTntMinecart extends TridentMinecart implements TNTMinecart {
    public TridentTntMinecart(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public int fuseTime() {
        return 0;
    }

    @Override
    public void setFuseTime(int time) {

    }

    @Override
    public EntityType type() {
        return EntityType.TNT_MINECART;
    }
}
