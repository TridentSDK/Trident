package net.tridentsdk.server.entity.projectile;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.ProjectileLauncher;
import net.tridentsdk.entity.projectile.FishHook;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;

import java.util.UUID;

/**
 * Represents a fishing hook
 *
 * @author The TridentSDK Team
 */
public class TridentFishHook extends TridentProjectile implements FishHook {
    public TridentFishHook(UUID uuid, Position spawnPosition, ProjectileLauncher projectileLauncher) {
        super(uuid, spawnPosition, projectileLauncher);
    }

    @Override
    public void applyProperties(EntityProperties properties) {

    }

    @Override
    protected void hit() {

    }

    @Override
    public float biteChance() {
        return 0;
    }

    @Override
    public void setBiteChance(float chance) {

    }

    @Override
    public EntityType type() {
        return EntityType.FISH_HOOK;
    }
}
