package net.tridentsdk.server.entity.projectile;

import net.tridentsdk.Position;
import net.tridentsdk.entity.Projectile;
import net.tridentsdk.entity.living.ProjectileLauncher;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;

import java.util.UUID;

/**
 * Represents an experience bottle that is thrown
 *
 * @author The TridentSDk Team
 */
public class TridentExpBottle extends TridentProjectile implements Projectile {
    public TridentExpBottle(UUID uuid, Position spawnPosition, ProjectileLauncher projectileLauncher) {
        super(uuid, spawnPosition, projectileLauncher);
    }

    @Override
    public void applyProperties(EntityProperties properties) {

    }

    @Override
    protected void hit() {

    }

    @Override
    public EntityType type() {
        return EntityType.EXPERIENCE_BOTTLE;
    }
}
