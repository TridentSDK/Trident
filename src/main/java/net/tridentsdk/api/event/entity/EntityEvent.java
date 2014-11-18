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
