package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.Guardian;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;

import java.util.UUID;

/**
 * Represents a guardian entity
 *
 * @author The TridentSDK Team
 */
public class TridentGuardian extends TridentLivingEntity implements Guardian {
    public TridentGuardian(UUID uuid, Position spawnPosition) {
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
    public boolean isElder() {
        return false;
    }

    @Override
    public EntityType type() {
        return EntityType.GUARDIAN;
    }
}
