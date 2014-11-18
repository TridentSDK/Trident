/*
 *     TridentSDK - A Minecraft Server API
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
package net.tridentsdk.entity.living;

import net.tridentsdk.api.entity.Hostile;

/**
 * Represents a Creeper
 *
 * @author TridentSDK Team
 */
public interface Creeper extends Hostile {
    /**
     * Whether or not this creeper is powered (Struck by lightning)
     *
     * @return whether or not this creeper is powered
     */
    boolean isPowered();

    /**
     * Set whether or not this creeper is powered
     *
     * @param powered whether the creeper should be powered or not
     */
    void setPowered(boolean powered);

    /**
     * Gets this creeper's explosion radius
     *
     * @return this creeper's explosion radius
     */
    float getExplosionRadius();

    /**
     * Sets this creeper's explosion radius
     *
     * @param rad radius to change to
     */
    void setExplosionRadius(float rad);
}
