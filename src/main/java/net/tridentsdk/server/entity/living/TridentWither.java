package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.Wither;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.server.entity.TridentLivingEntity;

import java.util.UUID;

/**
 * Represents a Wither boss entity
 *
 * @author The TridentSDK Team
 */
public class TridentWither extends TridentLivingEntity implements Wither {
    public TridentWither(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
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
        return EntityType.WITHER;
    }
}
