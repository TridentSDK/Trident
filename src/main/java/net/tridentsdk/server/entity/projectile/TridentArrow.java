package net.tridentsdk.server.entity.projectile;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.ProjectileLauncher;
import net.tridentsdk.entity.projectile.Arrow;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;

import java.util.UUID;

/**
 * Represents a launchable arrow in flight
 *
 * @author The TridentSDK Team
 */
public class TridentArrow extends TridentProjectile implements Arrow {
    public TridentArrow(UUID uuid, Position spawnPosition, ProjectileLauncher projectileLauncher) {
        super(uuid, spawnPosition, projectileLauncher);
    }

    @Override
    public boolean canPickup() {
        return false;
    }

    @Override
    public void setPickup(boolean pickup) {

    }

    @Override
    public void applyProperties(EntityProperties properties) {

    }

    @Override
    protected void hit() {

    }

    @Override
    public EntityType type() {
        return EntityType.ARROW;
    }
}
