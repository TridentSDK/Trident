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
package net.tridentsdk.api.entity.block;

import net.tridentsdk.api.entity.Equippable;
import net.tridentsdk.api.entity.SlotProperties;
import net.tridentsdk.api.util.PartRotation;

/**
 * Represents an Armor Stand
 *
 * @author TridentSDK Team
 */
public interface ArmorStand extends Equippable {
    /**
     * Gets the slot properties of this Armor Stand
     *
     * @return this armor stand's slot properties
     */
    SlotProperties getSlotProperties();

    /**
     * Whether or not this Armor Stand is invisible
     *
     * @return whether or not this Armor Stand is invisible
     */
    boolean isInvisible();

    /**
     * Whether or not this Armor Stand should display its baseplate
     *
     * @return whether or not this Armor Stand should display its baseplate
     */
    boolean displayBaseplate();

    /**
     * Whether or not this Armor Stand should display its arms
     *
     * @return whether or not this Armor Stand should display its arms
     */
    boolean displayArms();

    /**
     * Whether or not this Armor Stand will fall or not
     *
     * @return whether or not this Armor Stand will fall or not
     */
    boolean useGravity();

    /**
     * Returns the pose for this Armor Stand
     *
     * @return the post of this Armor Stand
     * @deprecated Uses magic numbers for indexing, exists until another way is pushed
     */
    @Deprecated
    PartRotation[] getPose();

    /**
     * Whether or not this Armor Stand is small
     *
     * @return whether or not this Armor Stand is small
     */
    boolean isTiny();
}
