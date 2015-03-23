package net.tridentsdk.server.entity.projectile;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.ProjectileLauncher;
import net.tridentsdk.entity.projectile.Potion;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;

import java.util.UUID;

/**
 * Represents a thrown potion
 *
 * @author The TridentSDK Team
 */
public class TridentPotion extends TridentProjectile implements Potion {
    public TridentPotion(UUID uuid, Position spawnPosition, ProjectileLauncher projectileLauncher) {
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
        return EntityType.POTION;
    }
}
