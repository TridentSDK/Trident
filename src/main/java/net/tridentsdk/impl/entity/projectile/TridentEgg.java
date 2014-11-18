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
package net.tridentsdk.impl.entity.projectile;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.living.ProjectileSource;
import net.tridentsdk.api.entity.projectile.Egg;
import net.tridentsdk.impl.entity.TridentProjectile;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents an egg after being thrown
 *
 * @author The TridentSDK Team
 */
public class TridentEgg extends TridentProjectile implements Egg {
    /**
     * Inherits constructor from {@link net.tridentsdk.impl.entity.TridentProjectile}
     */
    public TridentEgg(UUID uniqueId, Location spawnLocation, ProjectileSource source) {
        super(uniqueId, spawnLocation, source);
    }

    @Override
    public void applyProperties(EntityProperties properties) {
    }

    @Override
    protected void hit() {
        int chance = ThreadLocalRandom.current().nextInt(1, 256);

        if (chance <= 8) {
            if (chance == 1) {
                // TODO: Spawn 4 Baby Chicken
            } else {
                // TODO: Spawn 1 Baby Chicken
            }
        }
    }
}
