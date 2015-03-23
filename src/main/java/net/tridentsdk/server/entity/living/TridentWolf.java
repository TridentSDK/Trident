package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.base.SubstanceColor;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.Wolf;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;

import java.util.UUID;

/**
 * Represents a wolf
 *
 * @author The TridentSDK Team
 */
public class TridentWolf extends TridentLivingEntity implements Wolf {
    public TridentWolf(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public boolean isAngry() {
        return false;
    }

    @Override
    public SubstanceColor collarColor() {
        return null;
    }

    @Override
    public boolean isTamed() {
        return false;
    }

    @Override
    public UUID owner() {
        return null;
    }

    @Override
    public boolean isSitting() {
        return false;
    }

    @Override
    public int age() {
        return 0;
    }

    @Override
    public void setAge(int ticks) {

    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean isInLove() {
        return false;
    }

    @Override
    public boolean isHostile() {
        return false;
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
        return EntityType.WOLF;
    }
}
