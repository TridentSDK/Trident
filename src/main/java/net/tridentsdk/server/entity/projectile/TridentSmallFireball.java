package net.tridentsdk.server.entity.projectile;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.ProjectileLauncher;
import net.tridentsdk.entity.projectile.SmallFireball;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;

import java.util.UUID;

/**
 * Represents a small fireball
 *
 * @author The TridentSDK Team
 */
public class TridentSmallFireball extends TridentProjectile implements SmallFireball {
    public TridentSmallFireball(UUID uuid, Position spawnPosition, ProjectileLauncher projectileLauncher) {
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
        return EntityType.SMALL_FIREBALL;
    }
}
