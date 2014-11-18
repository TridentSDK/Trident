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

/**
 * Called when an entity catches fire
 */
public abstract class EntityBurnEvent extends EntityEvent {
    private boolean cancelled;
    private int fireTicks;

    public EntityBurnEvent(Entity entity, int fireTicks) {
        super(entity);
        this.fireTicks = fireTicks;
    }

    /**
     * Gets how long this entity will be on fire for, in ticks
     */
    public int getFireTicks() {
        return this.fireTicks;
    }

    /**
     * Sets how long this entity will be on fire for, in ticks
     */
    public void setFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
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
