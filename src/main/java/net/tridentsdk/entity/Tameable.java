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

import java.util.UUID;

/**
 * Represents a tameable entity
 *
 * @author TridentSDK Team
 */
public interface Tameable extends Ageable {
    /**
     * Whether or not this entity is tamed
     *
     * @return whether or not this entity is tamed
     */
    boolean isTamed();

    /**
     * The UUID of this entity's owner
     *
     * @return the UUID of the {@link net.tridentsdk.entity.living.Player}Player that owns this entity, {@code null}
     * if untamed
     */
    UUID getOwner();

    /**
     * Whether or not this entity is sitting
     *
     * @return whether or not this entity is sitting
     */
    boolean isSitting();
}
