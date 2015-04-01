package net.tridentsdk.server.entity;

import net.tridentsdk.Position;
import net.tridentsdk.entity.Firework;
import net.tridentsdk.entity.traits.FireworkProperties;
import net.tridentsdk.entity.types.EntityType;

import java.util.UUID;

/**
 * Represents a firable firework that explodes after a set amount of time
 *
 * @author The TridentSDK Team
 */
public class TridentFirework extends TridentEntity implements Firework {
    public TridentFirework(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public int timeLived() {
        return 0;
    }

    @Override
    public FireworkProperties properties() {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.FIREWORK;
    }
}
