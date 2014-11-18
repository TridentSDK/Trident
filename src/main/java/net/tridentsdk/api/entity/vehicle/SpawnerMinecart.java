/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
