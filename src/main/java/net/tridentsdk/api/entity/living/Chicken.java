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
package net.tridentsdk.api.entity.living;

import net.tridentsdk.api.entity.Ageable;
import net.tridentsdk.api.entity.Peaceful;

/**
 * Represents a Chicken
 *
 * @author TridentSDK Team
 */
public interface Chicken extends Ageable, Peaceful {
    /**
     * Whether or not this Chicken is a 'Chicken Jockey', defined by whether or not this Chicken will naturally despawn
     *
     * @return whether or not this Chicken is a 'Chicken Jockey'
     */
    boolean isChickenJockey();

    /**
     * Ticks until this Chicken will lay its egg
     *
     * @return the number of ticks until this Chicken will lay an egg
     */
    int getEggTicks();
}
