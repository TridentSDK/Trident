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
package net.tridentsdk.api.event.entity;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.Entity;

public class EntitySpawnEvent extends EntityEvent {

    private final Location location;
    private boolean cancel;

    /**
     * @param entity   the entity spawned
     * @param location the location of the spawning
     */

    public EntitySpawnEvent(Entity entity, Location location) {
        super(entity);
        this.location = location;
    }

    /**
     * @return return the location where the entity was spawned
     */

    public Location getLocation() {
        return this.location;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
