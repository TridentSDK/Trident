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

import net.tridentsdk.entity.*;

/**
 * Represents a Horse
 *
 * @author TridentSDK Team
 */
public interface Horse extends Tameable, Saddleable, InventoryHolder, Peaceful {
    /**
     * What breed of horse this is
     *
     * @return the HorseType that represents this breed
     */
    HorseType getBreed();

    /**
     * Whether or not this horse is grazing
     *
     * @return if this horse is grazing or not
     */
    boolean isGrazing();

    /**
     * The temper of this horse, higher temper dictates that the horse is easier to tame
     *
     * @return the temper of this horse. Range of 0-100
     */
    int getTemper();

    /**
     * Whether or not this horse has a chest
     *
     * @return false if this horse's breed is not a donkey or mule, or this horse has no chest
     */
    boolean hasChest();

    /**
     * The variant of this horse, will return an invalid variant if this horse is not of a HORSE breed
     *
     * @return the variant of this horse
     */
    HorseVariant getVariant();
}
