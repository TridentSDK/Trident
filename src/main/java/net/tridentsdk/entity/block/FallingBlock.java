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
package net.tridentsdk.entity.block;

import net.tridentsdk.entity.Entity;

/**
 * Represents a dynamic tile
 *
 * @author TridentSDK Team
 */
public interface FallingBlock extends Entity {
    /**
     * The state this FallingBlock represents
     *
     * @return the BlockState of this falling block
     */
    Object getState();  /* TODO: Change return type to valid implementation of BlockState */

    /**
     * Whether or not this FallingBlock should drop when it breaks
     *
     * @return whether or not this FallingBlock should drop its item when it breaks
     */
    boolean shouldDrop();
}
