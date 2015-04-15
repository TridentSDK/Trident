package net.tridentsdk.server.entity.vehicle;

import net.tridentsdk.Position;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.vehicle.Boat;
import net.tridentsdk.server.entity.TridentEntity;

import java.util.UUID;

/**
 * Represents a boat
 *
 * @author The TridentSDK Team
 */
public class TridentBoat extends TridentEntity implements Boat {
    public TridentBoat(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public EntityType type() {
        return EntityType.BOAT;
    }
}
