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
import net.tridentsdk.api.event.Cancellable;
import net.tridentsdk.api.event.Event;

public class EntityEvent extends Event implements Cancellable {

    private Entity entity;

    private boolean isCancelled;

    public EntityEvent(Entity entity) {
        this(entity, false);
    }

    public EntityEvent(Entity entity, boolean async) {
        super(async);
        this.setEntity(entity);
    }

    /**
     * @return return entity associated with this event
     */

    public Entity getEntity() {
        return this.entity;
    }

    /**
     * set the entity associated with this event
     */
    protected void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
