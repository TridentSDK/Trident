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
 * Represents a living entity that can wear a saddle
 *
 * @author TridentSDK Team
 */
public interface Saddleable extends LivingEntity {

    /**
     * Whether this entity is saddled or not
     *
     * @return whether or not this entity has a saddle
     */
    boolean isSaddled();

    /**
     * Set whether or not this entity is saddled
     *
     * @param saddled whether this entity should be saddled or not
     */
    void setSaddled(boolean saddled);
}
