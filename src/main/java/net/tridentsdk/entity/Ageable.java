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
package net.tridentsdk.entity;

import net.tridentsdk.api.entity.LivingEntity;

/**
 * Represents a LivingEntity that has an age and has the ability to bread
 *
 * @author TridentSDK Team
 */
public interface Ageable extends LivingEntity {
    /**
     * The current age of this entity, in ticks
     *
     * @return the age of this entity
     */
    int getAge();

    /**
     * Set the current age of this entity, in ticks
     *
     * @param ticks the age to set
     */
    void setAge(int ticks);

    /**
     * Whether or not this entity can breed or not, where the ability to breed represents whether or not this entity can
     * become "in love"
     *
     * @return whether or not this entity can be bred
     */
    boolean canBreed();

    /**
     * Whether or not this entity is "in love", such that it will actively display the particle effect for breeding
     * hearts and search for a mate
     *
     * @return whether or not this entity is in love
     */
    boolean isInLove();
}
