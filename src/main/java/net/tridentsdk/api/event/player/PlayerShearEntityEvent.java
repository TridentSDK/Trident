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
package net.tridentsdk.api.event.player;

import net.tridentsdk.api.entity.Item;
import net.tridentsdk.api.entity.LivingEntity;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;

/**
 * Called when a player shears an entity
 */
public class PlayerShearEntityEvent extends PlayerEvent implements Cancellable {
    private final LivingEntity sheared;
    private Item drop;
    private boolean cancelled;

    public PlayerShearEntityEvent(Player player, LivingEntity sheared, Item drop) {
        super(player);
        this.sheared = sheared;
        this.drop = drop;
    }

    /**
     * Gets the entity that was sheared
     */
    public LivingEntity getSheared() {
        return this.sheared;
    }

    /**
     * Gets the item that shearing this entity will drop
     */
    public Item getDrop() {
        return this.drop;
    }

    /**
     * Sets the item that shearing this entity will drop
     */
    public void setDrop(Item drop) {
        this.drop = drop;
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
