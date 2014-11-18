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
package net.tridentsdk.api.entity;

import net.tridentsdk.api.entity.living.ProjectileSource;

/**
 * Represents a Projectile
 *
 * @author TridentSDK Team
 */
public interface Projectile extends Entity {
    /**
     * Performs hit action
     */
    void doHit();

    /**
     * Returns the block/entity that was impaled by the projectile
     *
     * @return the impaled object by the projectile
     */
    Impalable getImpaled();

    /**
     * Returns the shooter of the Projectile
     *
     * @param shooter the ProjectileSource of the Projectile
     */
    void setSource(ProjectileSource shooter);

    /**
     * The projectile source
     *
     * @return gets the source of the projectile
     */
    ProjectileSource getProjectileSource();
}
