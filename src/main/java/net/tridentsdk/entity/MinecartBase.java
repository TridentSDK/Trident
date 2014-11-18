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

import net.tridentsdk.api.entity.*;

/**
 * Represents the generic Minecart
 *
 * @author TridentSDK Team
 */
public interface MinecartBase extends net.tridentsdk.api.entity.Entity {
    /**
     * Represents this Minecart's display tile, in the form of a BlockState
     *
     * @return the display tile of this Minecart
     */
    Object getDisplayTile();    /* TODO: Change return type to valid implementation of BlockState */

    /**
     * Set this Minecart's display tile to the specified block state
     *
     * @param blockState the state to set this to
     */
    void setDisplayTile(Object blockState);    /* TODO: Change param type to valid implementation of BlockState */

    /**
     * Get the offset for this Minecart's display tile
     *
     * @return the offset for this Minecart's display tile
     */
    int getDisplayTileOffset();

    /**
     * Set the offset for this Minecart's display tile
     *
     * @param offset the offset to set
     */
    void setDisplayTileOffset(int offset);

    /**
     * Gets the custom name of this Minecart
     *
     * @return the custom name of this Minecart
     */
    String getName();

    /**
     * Sets the custom name of this Minecart
     *
     * @param name the new value of the custom name
     */
    void setName(String name);
}
