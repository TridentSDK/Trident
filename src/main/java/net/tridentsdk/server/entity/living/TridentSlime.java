package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.Slime;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;

import java.util.UUID;

/**
 * Represents a slime
 *
 * @author The TridentSDK Team
 */
public class TridentSlime extends TridentLivingEntity implements Slime {
    public TridentSlime(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public EntityDamageEvent lastDamageEvent() {
        return null;
    }

    @Override
    public Player lastPlayerDamager() {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.SLIME;
    }
}
