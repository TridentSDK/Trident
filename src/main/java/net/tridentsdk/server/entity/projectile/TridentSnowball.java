/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server.entity.projectile;

import net.tridentsdk.Location;
import net.tridentsdk.entity.EntityProperties;
import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.living.Blaze;
import net.tridentsdk.entity.living.EnderDragon;
import net.tridentsdk.entity.living.ProjectileSource;
import net.tridentsdk.server.entity.TridentProjectile;

import java.util.UUID;

/**
 * Represents a snowball which is thrown
 *
 * @author The TridentSDK Team
 */
public class TridentSnowball extends TridentProjectile {
    /**
     * Inherits from {@link net.tridentsdk.server.entity.TridentProjectile}
     */
    public TridentSnowball(UUID uniqueId, Location spawnLocation, ProjectileSource source) {
        super(uniqueId, spawnLocation, source);
    }

    @Override
    public void applyProperties(EntityProperties properties) {
    }

    @Override
    protected void hit() {
        if (this.impaled != null && this.impaled.isImpaledEntity()) {
            if (this.impaled.impaledEntity() instanceof LivingEntity) {
                LivingEntity ent = (LivingEntity) this.impaled.impaledEntity();

                if (ent instanceof Blaze) {
                    // Damage 3 hearts
                } else if (ent instanceof EnderDragon) {
                    // Damage 1 heart
                } else {
                    // Damage 0 hearts (knockback)
                }
            }
        } else {
            this.remove();
        }
    }
}
