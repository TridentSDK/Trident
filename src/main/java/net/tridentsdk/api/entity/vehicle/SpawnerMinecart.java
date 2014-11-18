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
package net.tridentsdk.api.entity.vehicle;

import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.EntityType;
import net.tridentsdk.api.entity.MinecartBase;

/**
 * Represents a Spawner Minecart
 *
 * @author TridentSDK Team
 */
public interface SpawnerMinecart extends MinecartBase {

    /**
     * The spawn type of entities spawned by this Spawner Minecart
     *
     * @return the type of entity
     */
    EntityType getSpawnType();

    /**
     * The properties that will be applied when an Entity is spawned by this
     *
     * @return the properties applied
     */
    EntityProperties getAppliedProperties();
}
