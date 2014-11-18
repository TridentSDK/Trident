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

import net.tridentsdk.entity.Neutral;
import net.tridentsdk.entity.Tameable;

/**
 * Represents a Wolf
 *
 * @author TridentSDK Team
 */
public interface Wolf extends Tameable, Neutral {
    /**
     * Whether or not this entity is angry
     *
     * @return whether or not this entity is angry
     */
    boolean isAngry();

    /**
     * The color of this entity's collar
     *
     * @return the color of this entity's collar
     */
    Object getCollarColor();    /* TODO: Decide valid implementation for this along with Sheep and Wool  */
}
