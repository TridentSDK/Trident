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

import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.living.Player;

/**
 * Called when an entity is tamed, i.e. a horse or ocelot
 */
public class EntityTameEvent extends EntityEvent {
    private final Player tamer;
    private boolean cancelled;

    public EntityTameEvent(Entity entity, Player tamer) {
        super(entity);
        this.tamer = tamer;
    }

    public Player getTamer() {
        return this.tamer;
    }

    @Override
    public boolean isCancelled() {

        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
