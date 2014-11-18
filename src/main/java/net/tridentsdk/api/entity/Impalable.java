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
package net.tridentsdk.api.entity;

import net.tridentsdk.api.Block;

import java.util.Collection;

/**
 * A tile or entity that can be hit by a projectile
 *
 * @author The TridentSDK Team
 */
public interface Impalable {
    /**
     * Whether or not the impalable is an entity
     *
     * @return {@code true} if the impaled object is an entity, {@code false} if it is a tile
     */
    boolean isImpaledEntity();

    /**
     * Whether or not the impalable is a block (tile)
     *
     * @return {@code true} if the impaled object is a block (tile), {@code false} if it is an entity
     */
    boolean isImpaledTile();

    /**
     * Gets the entity that was impaled by the projectile
     * <p/>
     * <p>Returns {@code null} if {@code isImpaledEntity == false}</p>
     *
     * @return the entity impaled by the projectile
     */
    Entity impaledEntity();

    /**
     * Gets the block (tile) that was impaled by the projectile
     * <p/>
     * <p>Returns {@code null} if {@code isImpaledTile == false}</p>
     *
     * @return the impaled block (tile)
     */
    Block impaledTile();

    /**
     * Places the projectile into the hit collection
     *
     * @param projectile the projectile that hit the impalable
     */
    void put(Projectile projectile);

    /**
     * Removes the projectile from the impalable
     *
     * @param projectile the projectile to remove from the impalable
     * @return {@code true} if the projectile was removed, {@code false} if the projectile isn't in the impalable,
     * or it doesn't exist
     */
    boolean remove(Projectile projectile);

    /**
     * Removes all projectiles from the impalable
     */
    void clear();

    /**
     * Gets the projectiles that impaled the object
     * <p/>
     * <p>The list is ordered by first is oldest available arrow, and the last projectile is the newest projectile
     * impaling the tile/entity.</p>
     * <p/>
     * <p>Returns {@code null} if the current impalable was never hit by a projectile, or
     * {@code impaledEntity == null && impaledTile == null}</p>
     * <p/>
     * <p>This is a immutable collection</p>
     *
     * @return the last projectile that impaled the object
     */
    Collection<Projectile> projectiles();
}
