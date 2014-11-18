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

import net.tridentsdk.api.Block;
import net.tridentsdk.api.entity.Entity;

/**
 * Called when an Entity is set on fire by a block
 */
public class EntityBurnByBlockEvent extends EntityBurnEvent {
    private final Block causer;

    public EntityBurnByBlockEvent(Entity entity, int fireTicks, Block causer) {
        super(entity, fireTicks);
        this.causer = causer;
    }

    /**
     * Gets the block that set this entity on fire
     */
    public Block getBurner() {
        return this.causer;
    }
}
