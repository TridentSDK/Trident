package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.base.SubstanceColor;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.Sheep;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.server.entity.TridentLivingEntity;

import java.util.UUID;

/**
 * Represents a sheep
 *
 * @author The TridentSDK Team
 */
public class TridentSheep extends TridentLivingEntity implements Sheep {
    public TridentSheep(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public SubstanceColor color() {
        return null;
    }

    @Override
    public boolean isShearable() {
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
    public EntityDamageEvent lastDamageEvent() {
        return null;
    }

    @Override
    public Player lastPlayerDamager() {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.SHEEP;
    }
}
