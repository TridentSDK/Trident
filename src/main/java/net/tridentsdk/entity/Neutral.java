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
 * Represents a neutral entity Purpose of interface is to provide ease-of-access to large groups of a single type (i.e.
 * 'Hostiles', 'Friendlies')
 *
 * @author TridentSDK Team
 */
public interface Neutral extends LivingEntity {
    /**
     * Whether or not this entity has been angered. Note, not all neutral entities can be angered. When an entity is
     * angered, it is considered hostile
     *
     * @return Whether this entity is angered or not
     */
    boolean isHostile();
}
