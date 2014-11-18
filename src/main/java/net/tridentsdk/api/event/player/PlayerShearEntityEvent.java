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
