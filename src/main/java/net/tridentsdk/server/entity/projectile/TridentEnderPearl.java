package net.tridentsdk.server.entity.projectile;

import net.tridentsdk.Position;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.ProjectileLauncher;
import net.tridentsdk.entity.projectile.Enderpearl;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;

import java.util.UUID;

/**
 * Represents an ender pearl in flight
 *
 * @author The TridentSDK Team
 */
public class TridentEnderPearl extends TridentProjectile implements Enderpearl {
    public TridentEnderPearl(UUID uuid, Position spawnPosition, ProjectileLauncher projectileLauncher) {
        super(uuid, spawnPosition, projectileLauncher);
    }

    @Override
    public void applyProperties(EntityProperties properties) {

    }

    @Override
    protected void hit() {
        ProjectileLauncher launcher;

        // In case the launcher had not had the time yet to
        // record the launch into the projectile
        while ((launcher = launcher()) == null) {
        }

        if (launcher instanceof Entity) {
            Entity entity = (Entity) launcher;
            entity.teleport(position());
        }
    }

    @Override
    public EntityType type() {
        return EntityType.ENDER_PEARL;
    }
}
